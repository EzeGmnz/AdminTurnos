from django.shortcuts import render
from rest_framework.views import APIView
from .permissions import IsOwner
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from rest_framework.permissions import IsAuthenticated

@method_decorator(csrf_exempt, name='dispatch')
class UserProfile(APIView):
	permission_classes = [IsAuthenticated | IsOwner]
	
	def post(self, request):
		return JsonResponse({
			'isProvider' : request.user.isProvider,
			'isClient' : request.user.isClient,
			})