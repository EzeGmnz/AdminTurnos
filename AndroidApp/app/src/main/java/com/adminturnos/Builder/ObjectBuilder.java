package com.adminturnos.Builder;

import com.adminturnos.Exceptions.ExceptionCouldNotBuild;

public interface ObjectBuilder<E> {

    /**
     * Builds Object from Json
     *
     * @param json json to build object from
     * @return object built
     */
    public abstract E build(String json) throws ExceptionCouldNotBuild;

}
