from rest_framework import permissions


class IsOwner(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return obj.serviceprovider == request.user


class IsProvider(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user.isprovider


class IsProviderPro(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user.isproviderpro


class IsClient(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user.isclient
