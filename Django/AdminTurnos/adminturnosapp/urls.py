from django.urls import path, include
from adminturnosapp.views import login_android_google, logout_android_google, test

urlpatterns = [
	path('login-android-google/', login_android_google.as_view()),
	path('logout-android/', logout_android_google.as_view()),
	path('test/', test.as_view()),
]