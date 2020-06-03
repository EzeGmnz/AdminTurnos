from django.urls import path

from restauth.views import *

urlpatterns = [
    path('login-android-google/', ExchangeForAccessTokenAndroid.as_view()),
    path('login-web-google/', ExchangeForAccessTokenWeb.as_view()),
    path('logout-android/', LogoutAndroidGoogle.as_view()),
]
