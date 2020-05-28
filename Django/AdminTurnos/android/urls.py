from django.urls import path

from .views import *

urlpatterns = [
    path('profile/', UserProfile.as_view()),
    path('profile/places/', PlacesOwned.as_view()),
    path('profile/job-requests/', ViewJobRequests.as_view()),
    path('new-place/', NewPlace.as_view()),
    path('drop-place/', DropPlace.as_view()),
    path('place-does/', PlaceDoes.as_view()),
    path('search-place/', SearchPlace.as_view()),
    path('new-job-request/', NewJobRequest.as_view()),

]
