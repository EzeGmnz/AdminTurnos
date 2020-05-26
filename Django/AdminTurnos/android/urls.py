from django.urls import path, include
from .views import UserProfile

urlpatterns = [
	path('profile/', UserProfile.as_view()),
]