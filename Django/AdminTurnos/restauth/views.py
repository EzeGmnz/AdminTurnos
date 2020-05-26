from .authentification import GoogleAuth
from django.views.decorators.csrf import csrf_exempt
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from django.contrib.auth import logout, login
from django.utils.decorators import method_decorator
from django.views import View
from django.http import JsonResponse

from rest_framework.permissions import IsAuthenticated

@method_decorator(csrf_exempt, name='dispatch')
class login_android_google(ObtainAuthToken):
	def post(self, request):
		try:
			id_token = request.POST.get('id_token')

			user = GoogleAuth().authenticate(request, True, id_token)
			
			login(request, user)
			access_token, _ = Token.objects.get_or_create(user = user)

			return JsonResponse({	'access_token' : access_token.key,
									'user_id' : user.pk,
									'isProviderPro' : user.isProviderPro})

		except ValueError:
			content = {'message': 'Invalid token'}
			return JsonResponse(content)

@method_decorator(csrf_exempt, name='dispatch')
class logout_android_google(View):

	def post(self, request):
		token = request.POST.get('id_token')
		user = GoogleAuth().authenticate(request, token, True)
		
		logout(request)
		return None

