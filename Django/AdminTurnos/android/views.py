import datetime
import json

from django.core.exceptions import ValidationError
from django.db import IntegrityError
from django.db.models import Count
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from restauth.models import CustomUser
from .models import Place, Placedoes, JobRequest, Job, Service, DaySchedule, Provides, Appointment, Serviceinstance, \
    Promotion, Promoincludes, Jobtype, ScheduleTemplate
from .permissions import IsOwner, IsProvider, IsProviderPro
from .serializers import PlaceSerializer, JobRequestSerializer, ServiceSerializer, JobSerializer, \
    ServiceInstanceSerializer, AppointmentSerializer, DayScheduleSerializer, ProvidesSerializer, \
    ScheduleTemplateSerializer


# custom api view implementing get_object with permission check
class CustomAPIView(APIView):
    authentication_classes = [TokenAuthentication]
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
        params = request.POST

        place = Place()
        place.serviceprovider = request.user
        place.address = params.get('address')
        place.businessname = params.get('businessname')
        place.phonenumber = params.get('phonenumber')
        place.email = ""

        place.save()

        if params.get('works_here') == "true":
            job = Job()
            job.serviceprovider = request.user
            job.place = place
            job.save()

        return JsonResponse({'place_id': place.id})


@method_decorator(csrf_exempt, name='dispatch')
class DropPlace(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place_id
    def post(self, request):
        place = self.get_object(request, Place, request.POST.get('place_id'))
        place.delete()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class JobTypesView(CustomAPIView):
    # permission_classes = [IsAuthenticated & IsOwner]

    def get(self, request):
        jobtypes_list = Jobtype.objects.all()

        name_list = []
        for x in jobtypes_list:
            name_list.append(x.type)

        return JsonResponse({'jobtypes': name_list})


@method_decorator(csrf_exempt, name='dispatch')
class PlaceDoes(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    # place, jobtype
    def post(self, request):
        body = json.loads(request.body)
        place = self.get_object(request, Place, body['place'])
        jobtypes = body['jobtypes']

        placedoes_created = []
        for x in jobtypes:
            placedoes = Placedoes()
            jobtype = Jobtype.objects.get(type=x)
            placedoes.jobtype = jobtype
            placedoes.place = place

            try:
                placedoes.full_clean()
                placedoes_created.append(placedoes)
            except IntegrityError:
                return self.returnError(400, x)

        for x in placedoes_created:
            x.save()

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
        jobs = Job.objects.filter(serviceprovider=request.user)
        output = {}
        for j in jobs:
            schedules_list = {}
            day_schedules = DaySchedule.objects.filter(job=j)
            for ds in day_schedules:
                provides_list = []
                provides = Provides.objects.filter(job=j, day_schedule=ds)

                for p in provides:
                    provides_list.append(ProvidesSerializer(p).data)

                schedules_list[ds.day_of_week] = {
                    "day_schedule": DayScheduleSerializer(ds).data,
                    "provides": provides_list
                }

            output[j.id] = {
                "job": JobSerializer(j).data,
                "day_schedules": schedules_list
            }

        return JsonResponse({'jobs': output})


@method_decorator(csrf_exempt, name='dispatch')
class SearchPlace(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def apply_transformation(self, string):
        return string.strip().lower()

    def get(self, request):
        businessname = request.GET.get('searchquery')
        businessname = self.apply_transformation(businessname)
        output = []
        print(businessname)
        if businessname is not None and businessname != '':
            places = Place.objects.filter(businessname__icontains=businessname)
            for p in places:
                if p.serviceprovider != request.user:
                    output.append(PlaceSerializer(p).data)
        return JsonResponse({'places': output})


@method_decorator(csrf_exempt, name='dispatch')
class NewJobRequest(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def post(self, request):
        place_id = request.POST.get('place_id')

        if Job.objects.filter(place=place_id, serviceprovider=request.user.id).exists():
            return self.returnError(500, 'Job already exists')

        place = Place.objects.get(id=place_id)

        job_request = JobRequest()
        job_request.place = place
        job_request.serviceprovider = request.user
        job_request.save()
        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class ViewJobRequests(CustomAPIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        places = Place.objects.filter(serviceprovider=request.user.id)
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

    def post(self, request):
        place_id = request.POST.get('place_id')
        from_who = request.POST.get('serviceprovider')

        user_from = CustomUser.objects.get(email=from_who)
        place = self.get_object(request, Place, place_id)

        try:
            jr = JobRequest.objects.get(place=place, serviceprovider=user_from)

            job = Job()
            job.serviceprovider = user_from
            job.place = place

            job.save()
            jr.delete()

            return self.returnOK()
        except JobRequest.DoesNotExist:
            raise


@method_decorator(csrf_exempt, name='dispatch')
class CancelJobRequest(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        place_id = request.POST.get('place_id')
        from_who = request.POST.get('serviceprovider')

        user_from = CustomUser.objects.get(email=from_who)
        place = self.get_object(request, Place, place_id)

        try:
            jr = JobRequest.objects.get(place=place, serviceprovider=user_from)
            jr.delete()

            return self.returnOK()
        except JobRequest.DoesNotExist:
            raise


@method_decorator(csrf_exempt, name='dispatch')
class DoableServices(CustomAPIView):
    permission_classes = [IsAuthenticated & IsProvider]

    def get(self, request):
        place_id = request.GET.get('place_id')

        output = {}
        jobtypes = Placedoes.objects.filter(place_id=place_id)
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

        job_day_schedules = DaySchedule.objects.filter(job=job)
        for ds in job_day_schedules:
            if str(ds.day_of_week) not in body['days']:
                ds.delete()

        for day in body['days']:
            day_schedule = DaySchedule.objects.filter(job=job, day_of_week=day)
            config = body['days'][day]

            pause_start = config['pause_start'] if 'pause_start' in config else None
            pause_end = config['pause_end'] if 'pause_end' in config else None

            if len(day_schedule) == 0:
                day_schedule = DaySchedule(
                    job=job,
                    day_of_week=int(day),
                    day_start=config['day_start'],
                    day_end=config['day_end'],
                    pause_start=pause_start,
                    pause_end=pause_end,
                )
            else:
                day_schedule = day_schedule[0]
                day_schedule.day_start = config['day_start']
                day_schedule.day_end = config['day_end']
                day_schedule.pause_start = pause_start
                day_schedule.pause_end = pause_end

            day_schedule.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class NewProvides(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        body = json.loads(request.body)
        job = self.get_object(request, Job, body['job_id'])

        day_schedule = DaySchedule.objects.get(job=job, day_of_week=body['day_of_week'])
        provides_in_dayschedule = Provides.objects.filter(day_schedule=day_schedule)

        for provides in provides_in_dayschedule:
            if str(provides.id) not in body['services']:
                provides.delete()

        provides_list = []
        for service_id in body['services']:
            p = Provides.objects.filter(day_schedule=day_schedule, service_id=service_id)

            if len(p) == 0:
                p = Provides(
                    job=job,
                    day_schedule=day_schedule,
                    service=Service.objects.get(id=int(service_id)),
                    cost=body['costs'][str(service_id)],
                    duration=body['durations'][str(service_id)],
                    parallelism=body['parallelisms'][str(service_id)],
                )
                p.full_clean()
            else:
                p = p[0]
                p.cost = body['costs'][str(service_id)]
                p.duration = body['durations'][str(service_id)],
                p.parallelism = body['parallelisms'][str(service_id)],

            provides_list.append(p)

        for x in provides_list:
            x.save()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class RemoveProvides(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        day_schedule = DaySchedule.objects.get(id=request.POST.get('day_schedule_id'))
        provides = Provides.objects.get(id=request.POST.get('provides_id'))
        self.get_object(request, Job, day_schedule.job.id)
        provides.remove()

        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class RemoveDaySchedule(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        day_schedule = DaySchedule.objects.get(id=request.POST.get('day_schedule_id'))
        self.get_object(request, Job, day_schedule.job.id)
        day_schedule.remove()
        return self.returnOK()


@method_decorator(csrf_exempt, name='dispatch')
class DropJob(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def post(self, request):
        job = Job.objects.get(id=request.POST.get('job_id'))
        self.get_object(request, Place, job.place)
        job.delete()


@method_decorator(csrf_exempt, name='dispatch')
class GetAppointments(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner]

    def get(self, request):
        job_id = request.GET.get('job_id')
        job = Job.objects.get(id=job_id)

        if job.serviceprovider == request.user or job.place.serviceprovider == request.user:
            output = {}
            # TODO provide only future appointments
            appointments = Appointment.objects.filter(job=job, date__gte=datetime.date.today())
            for a in appointments:
                services = {}
                service_instances = Serviceinstance.objects.filter(appointment=a)
                for i, s in enumerate(service_instances):
                    services[i] = ServiceInstanceSerializer(s).data

                output[a.id] = {
                    'appointment': AppointmentSerializer(a).data,
                    'services': services
                }

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
            weekday_list = body['weekday_list']
            services_ids = body['services']
            discounts = body['discounts']

            promo = Promotion(job=job, since=since, to=to, description=description, weekday_list=weekday_list)
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
        job = self.get_object(request, Job, request.GET.get('job_id'))
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


@method_decorator(csrf_exempt, name='dispatch')
class ScheduleTemplateView(CustomAPIView):

    def get(self, request):
        output = []
        schedule_templates = ScheduleTemplate.objects.all()
        for x in schedule_templates:
            output.append(ScheduleTemplateSerializer(x).data)
        return JsonResponse({'schedule_templates': output})
