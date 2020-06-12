import json

from django.contrib.auth import logout, login
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views import View
from django.views.decorators.csrf import csrf_exempt
from rest_framework.authtoken.models import Token
from rest_framework.authtoken.views import ObtainAuthToken

from .authentification import GoogleAuth


@method_decorator(csrf_exempt, name='dispatch')
class ExchangeForAccessTokenAndroid(ObtainAuthToken):
    def post(self, request, **kwargs):
        try:
            id_token = request.POST.get('id_token')

            user = GoogleAuth().authenticate(request, True, id_token)
            login(request, user)
            access_token, _ = Token.objects.get_or_create(user=user)

            return JsonResponse({'access_token': access_token.key,
                                 'user_id': user.pk,
                                 'isclient': user.isclient,
                                 'isproviderpro': user.isproviderpro})

        except ValueError:
            content = {'message': 'Invalid token'}
            return JsonResponse(content)


@method_decorator(csrf_exempt, name='dispatch')
class ExchangeForAccessTokenWeb(ObtainAuthToken):
    def post(self, request, **kwargs):
        try:
            body = json.loads(request.body)
            id_token = body['id_token']

            user = GoogleAuth().authenticate(request, False, id_token)
            login(request, user)

            access_token, _ = Token.objects.get_or_create(user=user)

            return JsonResponse({'access_token': access_token.key,
                                 'user_id': user.pk,
                                 'isclient': user.isclient,
                                 'isproviderpro': user.isproviderpro})

        except ValueError as e:
            print(e)
            raise


@method_decorator(csrf_exempt, name='dispatch')
class LogoutAndroidGoogle(View):

    def post(self, request):
        token = request.POST.get('id_token')
        user = GoogleAuth().authenticate(request, token, True)

        logout(request)
        return None
