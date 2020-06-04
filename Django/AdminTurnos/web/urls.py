from django.urls import path
from django.views.generic import TemplateView

from web.views import *

urlpatterns = [
    path('new-appointment/', NewAppointment.as_view()),
    path('index/', TemplateView.as_view(template_name='web_log_in.html')),
    path('get-provided-services/', GetProvidedServices.as_view()),
    path('get-promotions/', GetPromotions.as_view()),
    path('get-available-appointments/', GetAvailableAppointments.as_view()),
]
