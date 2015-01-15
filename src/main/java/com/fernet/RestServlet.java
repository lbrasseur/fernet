package com.fernet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public class RestServlet<T> extends HttpServlet {
    private final T service;
    private final Gson gson;

    @Inject
    public RestServlet(T service, Gson gson) {
        this.service = checkNotNull(service);
        this.gson = checkNotNull(gson);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final String methodName = req.getRequestURI().substring(
                req.getRequestURI().lastIndexOf('/') + 1);
        Method method = Iterables.find(
                Arrays.asList(service.getClass().getMethods()),
                new Predicate<Method>() {
                    @Override
                    public boolean apply(Method method) {
                        return method.getName().equals(methodName);
                    }
                });
        try (Reader contentReader = new InputStreamReader(req.getInputStream())) {
            Object requestDto = gson.fromJson(contentReader,
                    method.getParameterTypes()[0]);
            Object responseDto = method.invoke(service, requestDto);
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            Throwables.propagate(e);
        }
    }
}