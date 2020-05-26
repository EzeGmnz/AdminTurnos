from django.urls import path, include
from restauth.views import login_android_google, logout_android_google

urlpatterns = [
	path('login-android-google/', login_android_google.as_view()),
	path('logout-android/', logout_android_google.as_view()),
]