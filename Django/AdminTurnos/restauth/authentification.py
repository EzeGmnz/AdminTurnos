from django.contrib.auth.backends import BaseBackend

from django.contrib import auth
from google.oauth2 import id_token
from google.auth.transport import requests
from django.conf import settings


class GoogleAuth(BaseBackend):
	def authenticate(self, request, isProvider, token=None):
		try:
			# Validating token id
			idinfo = id_token.verify_oauth2_token(token, requests.Request(), settings.GOOGLE_OAUTH_CLIENTID)

			if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
				raise ValueError('Wrong issuer.')

			# getting user id
			userid = idinfo['sub']
			UserModel = auth.get_user_model()
			user, _ = UserModel.objects.get_or_create(email = idinfo['email'], isProvider=isProvider)

			return user
		
		except ValueError as err:
			raise