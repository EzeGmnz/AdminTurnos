from django.urls import path

from .views import *

urlpatterns = [
    path('profile/', UserProfile.as_view()),
    path('profile/places/', PlacesOwned.as_view()),
    path('profile/jobs/', JobsView.as_view()),
    path('profile/job-requests/', ViewJobRequests.as_view()),
    path('new-place/', NewPlace.as_view()),
    path('drop-place/', DropPlace.as_view()),
    path('place-does/', PlaceDoes.as_view()),
    path('search-place/', SearchPlace.as_view()),
    path('new-job-request/', NewJobRequest.as_view()),
    path('profile/accept-job-request/', AcceptJobRequest.as_view()),
    path('doable-services/', DoableServices.as_view()),
    path('profile/new-day-schedule/', NewDaySchedule.as_view()),
    path('profile/new-provides/', NewProvides.as_view()),
]
