# Create your views here.
import datetime
import json
from functools import cmp_to_key

from django.core.exceptions import ValidationError
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated

from android.models import Job, Provides, Promotion, Promoincludes, Service, Appointment, Serviceinstance
from android.permissions import IsClient, IsOwner
from android.views import CustomAPIView


@method_decorator(csrf_exempt, name='dispatch')
class SetPreferredNotification(CustomAPIView):
    permission_classes = [IsAuthenticated & IsClient]

    def post(self, request):
        pass


@method_decorator(csrf_exempt, name='dispatch')
class GetProvidedServices(CustomAPIView):
    permission_classes = [IsAuthenticated & IsClient]

    def get(self, request):
        job_id = request.GET.get('job_id')
        date = request.GET.get('date')

        job = Job.objects.get(id=job_id)

        formated_date = datetime.datetime.strptime(date, '%d/%m/%Y')
        services = Provides.objects.filter(job=job, day_schedule__day_of_week=formated_date.weekday())
        output = {}
        for x in services:
            output[x.service.id] = {
                'jobtype': x.service.jobtype.type,
                'cost': x.cost,
                'duration': x.duration,
                'name': x.service.name,
            }
        return JsonResponse(output)


@method_decorator(csrf_exempt, name='dispatch')
class GetPromotions(CustomAPIView):
    permission_classes = [IsAuthenticated & IsClient]

    def get(self, request):
        job_id = request.GET.get('job_id')
        date = request.GET.get('date')

        job = Job.objects.get(id=job_id)
        formatted_date = datetime.datetime.strptime(date, '%d/%m/%Y')

        output = self.get_promos_date(job, formatted_date)
        return JsonResponse(output)

    def get_promos_date(self, job, date):
        backend_weekday = (date.weekday() + 1) % 7
        promo_list = Promotion.objects.filter(job=job, since__lte=date, to__gte=date)
        output = {}
        for promo in promo_list:
            for schedule_weekday in promo.weekday_list:
                if backend_weekday == schedule_weekday:
                    output[promo.id] = self.get_promotion_offer(promo)
        return output

    def get_promotion_offer(self, promo):
        includes = Promoincludes.objects.filter(promotion=promo)
        includes_list = {}
        for i in includes:
            includes_list[i.service.id] = i.discount
        return includes_list


@method_decorator(csrf_exempt, name='dispatch')
class GetAvailableAppointments(CustomAPIView):
    permission_classes = [IsAuthenticated & IsClient]

    class Division:
        def __init__(self, start, end):
            self.start = start
            self.end = end

        def serialize(self):
            return {"start": str(self.start), "end": str(self.end)}

    def get(self, request):
        job_id = request.GET.get('job_id')
        str_date = request.GET.get('date')
        service_ids = request.GET.get('services').split(",")
        job = Job.objects.get(id=job_id)
        date = datetime.datetime.strptime(str_date, '%d/%m/%Y')

        services = self.get_services(service_ids, job, date)
        services_timestamps_lists = self.get_div_lists(job, date, services)
        possible_appointments = self.get_adjacent_divs_posibilities(services_timestamps_lists)

        return JsonResponse({"divisions": self.serialize_divisions(service_ids, possible_appointments)})

    def serialize_divisions(self, service_ids, divisions):
        output = []
        for x in divisions:
            tup = {}
            for i, y in enumerate(x):
                tup[service_ids[i]] = y.serialize()
            output.append(tup)
        return output

    def get_services(self, service_ids, job, date):
        services = []
        for i in service_ids:
            s = Service.objects.get(id=i)
            services.append(s)

        return self.order_by_duration(job, date, services)

    def order_by_duration(self, job, date, services):
        output = []
        provides = []
        for s in services:
            p = Provides.objects.get(job=job,
                                     day_schedule__day_of_week=date.weekday(),
                                     service=s)
            provides.append(p)

        def compare(provides1, provides2):
            provides1_minutes = provides1.duration.hour * 60 + provides1.duration.minute
            provides2_minutes = provides2.duration.hour * 60 + provides2.duration.minute
            return provides2_minutes - provides1_minutes

        ordered_provides = sorted(provides, key=cmp_to_key(compare))

        for x in ordered_provides:
            output.append(x.service)
        return output

    def get_div_lists(self, job, date, services):
        output = []
        for s in services:
            possibles_starting_div = self.get_available_divisions(job, date, s)
            output.append(possibles_starting_div)
        return output

    def get_available_divisions(self, job, date, service):
        provides = Provides.objects.get(job=job,
                                        day_schedule__day_of_week=date.weekday(),
                                        service=service)

        # TODO consider day_schedule pauses
        output = []
        duration = provides.duration
        day_start = provides.day_schedule.day_start
        day_end = provides.day_schedule.day_end
        current_time = datetime.datetime.combine(date, day_start)

        durationdelta = datetime.timedelta(hours=duration.hour,
                                           minutes=duration.minute)

        last_appointment_time = datetime.datetime.combine(date, day_end) - durationdelta
        while current_time <= last_appointment_time:
            end = current_time + durationdelta
            start = current_time

            if not job.is_at_max_parallelism(service, start):
                division = self.Division(
                    start.time(),
                    end.time())
                output.append(division)

            current_time = end

        return output

    def get_adjacent_divs_posibilities(self, lists):
        output = []
        for x in lists[0]:
            output.append([x])
        current = []
        for i in range(1, len(lists)):
            for divs in output:
                for y in lists[i]:
                    if divs[-1].end == y.start:
                        tup = divs + [y]
                        current.append(tup)
            output = current
            current = []

        return output


@method_decorator(csrf_exempt, name='dispatch')
class NewAppointment(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner & IsClient]

    def clean(self):
        # TODO only one appointment (in the future) per client int his place only
        pass

    def post(self, request):
        # TODO apply promotions
        body = json.loads(request.body)
        date = datetime.datetime.strptime(body['date'], '%d/%m/%Y')

        appointment = Appointment(
            job_id=body['job_id'],
            client=request.user,
            date=date.date()
        )

        appointment.save()
        service_instance_list = []
        appointment_data = body['appointment']

        for x in body['appointment']:
            time = datetime.datetime.strptime(appointment_data[x]['start'], '%H:%M:%S').time()
            timestamp = datetime.datetime.combine(date, time)
            service_instance = Serviceinstance(
                appointment=appointment,
                timestamp=timestamp,
                service_id=x
            )

            service_instance_list.append(service_instance)

        try:
            for x in service_instance_list:
                x.save()
        except ValidationError:
            appointment.delete()
            raise

        return JsonResponse({})
