from django.conf import settings
from django.contrib import auth
from django.contrib.auth.backends import BaseBackend
from google.auth.transport import requests
from google.oauth2 import id_token


class GoogleAuth(BaseBackend):
    def authenticate(self, request, isProvider, token=None):
        try:
            # Validating token id
            idinfo = id_token.verify_oauth2_token(token, requests.Request(), settings.GOOGLE_OAUTH_CLIENTID)

            if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
                raise ValueError('Wrong issuer.')

            # getting user id
            userid = idinfo['sub']
            user_model = auth.get_user_model()
            user, _ = user_model.objects.get_or_create(email=idinfo['email'], isprovider=isProvider)

            return user

        except ValueError as err:
            raise
