import datetime
import json

from django.core.exceptions import ValidationError
from django.db.models import Count
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from .models import Place, Placedoes, JobRequest, Job, Service, DaySchedule, Provides, Appointment, Serviceinstance, \
    Promotion, Promoincludes
from .permissions import IsOwner, IsProvider, IsProviderPro
from .serializers import PlaceSerializer, JobRequestSerializer, ServiceSerializer, JobSerializer, \
    ServiceInstanceSerializer


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
        return JsonResponse({'reason': message}, status=code)


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

    # place, jobtype
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place'))
        place_does = Placedoes(place.id, request.POST.get('jobtype'))
        place_does.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class PlacesOwned(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider=request.user.id)
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
        place = request.data.get('place')

        output = {}
        jobtypes = Placedoes.objects.filter(place=place)
        for pd in jobtypes:

            services = Service.objects.filter(jobtype=pd.jobtype.type)
            services_json = []
            for s in services:
                services_json.append(ServiceSerializer(s).data)

            output[pd.jobtype.type] = services_json

        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class NewDaySchedule(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = self.get_object(request, Job, body['job_id'])

        for day in body['days']:
            config = body['days'][day]
            day_schedule = DaySchedule(
                job=job,
                day_of_week=int(day),
                day_start=config['day_start'],
                day_end=config['day_end'],
                pause_start=config['pause_start'],
                pause_end=config['pause_end'],
            )
            day_schedule.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class NewProvides(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = self.get_object(request, Job, body['job_id'])
        try:
            day_schedule = DaySchedule.objects.get(job=job, day_of_week=body['day_of_week'])
            print(body['day_of_week'])
            provides_list = []
            for service_id in body['services']:
                p = Provides(
                    job=job,
                    day_schedule=day_schedule,
                    service=Service.objects.get(id=int(service_id)),
                    cost=body['costs'][str(service_id)],
                    duration=body['durations'][str(service_id)],
                    parallelism=body['parallelisms'][str(service_id)],
                )
                p.full_clean()
                provides_list.append(p)

            for x in provides_list:
                x.save()

        except DaySchedule.DoesNotExist:
            return self.returnError(400, 'Day schedule does not exist')

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class DropProvides(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        job = self.get_object(request, Job, request.POST.get('job_id'))
        provides = Provides.objects.filter(job=job, service_id=request.POST.get('service_id'))
        for p in provides:
            p.delete()
        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class DropJob(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        job = Job.objects.get(id=request.POST.get('job_id'))
        place = self.get_object(request, Place, job.place)
        job.delete()


@method_decorator(csrf_exempt, name='dispatch')
class GetAppointments(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def get(self, request):
        job_id = request.data.get('job_id')
        date = request.data.get('date')
        job = Job.objects.get(id=job_id)

        if job.serviceprovider == request.user or job.place.serviceprovider == request.user:
            output = {}

            appointments = Appointment.objects.filter(job=job, date=date)
            for a in appointments:
                services = {}
                service_instances = Serviceinstance.objects.filter(appointment=a)
                for i, s in enumerate(service_instances):
                    services[i] = ServiceInstanceSerializer()
                output[a.id] = services

            return JsonResponse(output)

        return self.returnError(403, "Not allowed to view appointments")


@method_decorator(csrf_exempt, name='dispatch')
class DropAppointment(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        appointment = Appointment.objects.get(request.POST.get('appointment_id'))
        job = self.get_object(request, Job, appointment.job)

        appointment.delete()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class NewPromotion(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = Job.objects.get(id=body['job_id'])

        if job.serviceprovider == request.user or job.place.serviceprovider == request.user:
            since = body['since']
            to = body['to']
            description = body['description']
            day_schedules = body['day_schedules']
            services_ids = body['services']
            discounts = body['discounts']

            promo = Promotion(job=job, since=since, to=to, description=description, day_schedules=day_schedules)
            promo.save()
            includes_list = []
            for s in services_ids:
                service = Service.objects.get(id=s)
                promo_includes = Promoincludes(promotion=promo, service=service, discount=discounts[str(s)])
                try:
                    promo_includes.full_clean()
                    includes_list.append(promo_includes)
                except ValidationError:
                    promo.delete()
                    raise

            for x in includes_list:
                x.save()

            return self.returnOK()
        return self.returnError(403, 'Forbidden, you don\'t have permission to do that')


@method_decorator(csrf_exempt, name='dispatch')
class DropPromotion(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        promo = Promotion.objects.get(id=request.POST.body('promo_id'))
        self.get_object(request, Job, promo.job)
        promo.delete()
        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class GetFrequency(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner & IsProviderPro]

    def get(self, request):
        job = self.get_object(request, Job, request.data.get('job_id'))
        output = {}

        appointments = Appointment.objects.filter(job=job, date__lte=datetime.date.today()) \
            .values('client') \
            .annotate(appCount=Count('id'))

        for a in appointments:
            output[a['client']] = a['appCount']

        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class DropAppointmentsRange(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner & IsProviderPro]

    def post(self, request):
        job = self.get_object(request, Job, request.POST.get('job_id'))
        since = request.POST.get('since')
        to = request.POST.get('to')

        # TODO notify
        appointments = Appointment.objects.filter(job=job, date__range=[since, to])
        for a in appointments:
            a.delete()

        return self.returnOK()
