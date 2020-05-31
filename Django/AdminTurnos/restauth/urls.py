from django.urls import path

from restauth.views import LoginAndroidGoogle, LogoutAndroidGoogle

urlpatterns = [
    path('login-android-google/', LoginAndroidGoogle.as_view()),
    path('logout-android/', LogoutAndroidGoogle.as_view()),
]
