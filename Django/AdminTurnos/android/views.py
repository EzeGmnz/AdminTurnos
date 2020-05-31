import json

from django.http import HttpResponse
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from .models import Place, Placedoes, JobRequest, Job, Service, DaySchedule, Provides
from .permissions import IsOwner, IsProvider
from .serializers import PlaceSerializer, JobRequestSerializer, ServiceSerializer, JobSerializer


# custom api view implementing get_object with permission check
class CustomAPIView(APIView):
    responseOK = {'result': 'OK'}

    def get_object(self, request, obj_model, pk):
        obj = obj_model.objects.get(pk=pk)
        self.check_object_permissions(request, obj)
        return obj

    def returnOK(self):
        return JsonResponse(self.responseOK)

    def returnError(self, code, message):
        return HttpResponse(status=code, reason=message)


@method_decorator(csrf_exempt, name='dispatch')
class UserProfile(APIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def get(self, request):
        return JsonResponse({
            'isprovider': request.user.isprovider,
            'isclient': request.user.isclient,
        })


@method_decorator(csrf_exempt, name='dispatch')
class NewPlace(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    # street, streetnumber, apnumber, city, state, country, businessname, phonenumber, email
    def post(self, request):
        params = list(request.POST.values())
        params.insert(0, None)  # Autoincrement field
        params.insert(1, request.user.id)

        place = Place(*params)
        place.save()

        job = Job(None, request.user.id, place.id)
        job.save()
        return JsonResponse(PlaceSerializer(place).data)


@method_decorator(csrf_exempt, name='dispatch')
class DropPlace(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place_id
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place_id'))
        place.delete()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class PlaceDoes(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place_id, jobtype_type
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place_id'))
        place_does = Placedoes(place.id, request.POST.get('jobtype_type'))
        place_does.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class PlacesOwned(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider_owner_id=request.user.id)
        output = []
        for p in places:
            output.append(PlaceSerializer(p).data)
        return JsonResponse({'places': output})


@method_decorator(csrf_exempt, name='dispatch')
class JobsView(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def get(self, request):
        jobs = Job.objects.filter(serviceprovider_id=request.user.id)
        output = []
        for p in jobs:
            output.append(JobSerializer(p).data)
        return JsonResponse({'jobs': output})


@method_decorator(csrf_exempt, name='dispatch')
class SearchPlace(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        businessname = request.data.get('businessname')
        output = []
        if businessname is not None and businessname.strip() != '':
            places = Place.objects.filter(businessname__contains=businessname)
            for p in places:
                output.append(PlaceSerializer(p).data)
        return JsonResponse({'places': output})


@method_decorator(csrf_exempt, name='dispatch')
class NewJobRequest(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def post(self, request):
        place_id = request.POST.get('place_id')

        if Job.objects.filter(place=place_id, serviceprovider=request.user.id).exists():
            return self.returnError(500, 'Job already exists')

        job_request = JobRequest(place_id, request.user.id)
        job_request.save()
        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class ViewJobRequests(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider_owner_id=request.user.id)
        output = {}
        for p in places:
            job_requests = JobRequest.objects.filter(place=p.id)
            jb_list = []
            for jr in job_requests:
                jb_list.append(JobRequestSerializer(jr).data)
            output[p.id] = jb_list

        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class AcceptJobRequest(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # TODO add can cancel appointments

    def post(self, request):
        place_id = request.POST.get('place_id')
        from_who = request.POST.get('serviceprovider_from')

        place = self.get_object(request, Place, place_id)

        try:
            jr = JobRequest.objects.get(place=place.id, serviceprovider_from=from_who)
            job = Job(None, from_who, place.id)
            job.save()
            jr.delete()

            return self.returnOK()
        except JobRequest.DoesNotExist:
            raise


@method_decorator(csrf_exempt, name='dispatch')
class DoableServices(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def get(self, request):
        place_id = request.data.get('place_id')

        output = {}
        jobtypes = Placedoes.objects.filter(place=place_id)
        for pd in jobtypes:

            services = Service.objects.filter(jobtype_type=pd.jobtype_type.type)
            services_json = []
            for s in services:
                services_json.append(ServiceSerializer(s).data)

            output[pd.jobtype_type.type] = services_json

        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class NewDaySchedule(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = self.get_object(request, Job, body['job_id'])

        for day, config in body['days']:
            day_schedule = DaySchedule(
                job=job,
                day_of_week=int(day),
                day_start=config['day_start'],
                day_end=config['day_end'],
                pause_start=config['pause_start'],
                pause_end=config['pause_end'],
            )
            day_schedule.save()


@method_decorator(csrf_exempt, name='dispatch')
class NewProvides(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = self.get_object(request, Job, body['job_id'])
        day_schedule = DaySchedule.objects.get(job=job, day_of_week=body['day_of_week'])
        if not day_schedule.exists():
            self.returnError(500, 'Day schedule does not exist')

        for service_id in body['services']:
            p = Provides(
                job=job,
                day_schedule=day_schedule,
                service=Service.objects.get(id=int(service_id)),
                cost=body['costs'][service_id],
                duration=body['durations'][service_id],
                parallelism=body['parallelisms'][service_id],
            )
            p.save()
