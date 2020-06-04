# Create your views here.
import datetime

from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated

from android.models import Job, Provides, Promotion, Promoincludes
from android.permissions import IsOwner, IsClient
from android.views import CustomAPIView


@method_decorator(csrf_exempt, name='dispatch')
class NewAppointment(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner & IsClient]

    def clean(self):
        # TODO only one appointment (in the future) per client
        pass

    def post(self, request):
        # TODO apply promotions
        pass


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
        backend_weekday = (formated_date.weekday() + 1) % 7
        services = Provides.objects.filter(job=job, day_schedule__day_of_week=backend_weekday)
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

    def get(self, request):
        job_id = request.GET.get('job_id')
        date = request.GET.get('date')
        services = request.GET.get('services').split(",")
        job = Job.objects.get(id=job_id)
        formatted_date = datetime.datetime.strptime(date, '%d/%m/%Y')

        for x in services:
            print(x)

        return JsonResponse({})
