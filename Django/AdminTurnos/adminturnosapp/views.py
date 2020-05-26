from .authentification import GoogleAuth
from django.views.decorators.csrf import csrf_exempt
from rest_framework.response import Response
from .models import Test
from rest_framework.authtoken.models import Token
from django.contrib.auth import logout, login
from django.utils.decorators import method_decorator
from django.views import View
from django.http import JsonResponse

@method_decorator(csrf_exempt, name='dispatch')
class login_android_google(View):

	def post(self, request):
		token = request.POST.get('id_token')

		user = GoogleAuth().authenticate(request, True, token)
		
		login(request, user)
		access_token,_ = Token.objects.get_or_create(user = user)
		print(access_token)
		return JsonResponse({'access_token' : str(access_token)})

@method_decorator(csrf_exempt, name='dispatch')
class logout_android_google(View):

	def post(self, request):
		token = request.POST.get('id_token')
		user = GoogleAuth().authenticate(request, token, True)
		
		logout(request)
		return None

@method_decorator(csrf_exempt, name='dispatch')
class test(View):

	def get(self, request):
		if request.user.is_authenticated():
			if request.user.has_perm('can_access'):
				return Response(Test.objects.all())
