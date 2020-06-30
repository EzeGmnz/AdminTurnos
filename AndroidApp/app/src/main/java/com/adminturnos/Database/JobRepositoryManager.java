package com.adminturnos.Database;

import com.adminturnos.Listeners.RepositoryGetJobListListener;
import com.adminturnos.Listeners.RepositoryGetJobListener;
import com.adminturnos.Listeners.RepositorySaveListener;
import com.adminturnos.ObjectInterfaces.Job;

public interface JobRepositoryManager {

    void getJobs(RepositoryGetJobListListener listener);

    void saveJob(Job job, RepositorySaveListener listener);

    void getJob(String id, RepositoryGetJobListener listener);

    void invalidate();

    void saveJobToStorage(String jobId);
}
