from django.urls import path
from rest import views

urlpatterns = [
    path('rest/', views.serviceprovider_list),
    path('rest/<int:pk>/', views.serviceprovider_detail),
]