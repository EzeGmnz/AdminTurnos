from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('auth/', include('restauth.urls')),
    path('android/', include('android.urls')),
    path('web/', include('web.urls')),
]
