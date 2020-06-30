package com.adminturnos.Listeners;

import com.adminturnos.ObjectInterfaces.Job;

import java.util.List;

public interface RepositoryGetJobListListener {

    void onFetch(List<Job> jobList);

}
