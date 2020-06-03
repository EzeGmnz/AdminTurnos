# Create your views here.

from django.shortcuts import render
from django.template.loader import get_template
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated

from android.permissions import IsOwner, IsClient
from android.views import CustomAPIView


@method_decorator(csrf_exempt, name='dispatch')
class NewAppointment(CustomAPIView):
    permission_classes = [IsAuthenticated & IsOwner & IsClient]

    def post(self, request):
        pass


class Index(CustomAPIView):

    def get(self, request):
        return render(request, get_template('login.html'))
