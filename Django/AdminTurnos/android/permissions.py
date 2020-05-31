from rest_framework import permissions


class IsOwner(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return obj.serviceprovider == request.user.id


class IsItsJob(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return obj.serviceprovider == request.user.id


class IsProvider(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user.isprovider
