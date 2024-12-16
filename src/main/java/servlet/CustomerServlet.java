package servlet;


import DTO.CustomerDTO;
import com.google.gson.Gson;
import exception.DatabaseException;
import exception.DuplicateDataException;
import exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/customer/*")
public class CustomerServlet extends HttpServlet {
    private CustomerService service;

    @Override
    public void init() {
        service = new CustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CustomerDTO> result = new ArrayList<>();
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            try {
                result = service.getAll();
            } catch (DatabaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            } catch (NotFoundException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
        } else {
            String[] pathInfo = req.getPathInfo().substring(1).split("/");
            if (pathInfo.length == 1 && pathInfo[0].matches("\\d+")) {
                Integer customerId = Integer.parseInt(pathInfo[0]);
                try {
                    result = service.getById(customerId);
                } catch (DatabaseException e) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                } catch (NotFoundException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                    return;
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(new Gson().toJson(result));
        printWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index;
        try (BufferedReader reader = req.getReader()) {
            Gson gson = new Gson();
            CustomerDTO in = gson.fromJson(reader, CustomerDTO.class);
            try {
                index = service.add(in);
            } catch (DatabaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            } catch (DuplicateDataException | IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
        }
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter printWriter = resp.getWriter();
        printWriter.write("Successful addition. Index assigned:" + index);
        printWriter.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int index;
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required for deletion");
            return;
        }
        String[] pathInfo = req.getPathInfo().substring(1).split("/");
        if (pathInfo.length == 1 && pathInfo[0].matches("\\d+")) {
            Integer customerId = Integer.parseInt(pathInfo[0]);
            try {
                index = service.deleteById(customerId);
            } catch (DatabaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            } catch (NotFoundException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format.");
            return;
        }
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter printWriter = resp.getWriter();
        printWriter.write("Successful delete. Index deleted:" + index);
        printWriter.close();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int index;
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required for update");
            return;
        }
        String[] pathInfo = req.getPathInfo().substring(1).split("/");
        if (pathInfo.length == 1 && pathInfo[0].matches("\\d+")) {
            Integer customerId = Integer.parseInt(pathInfo[0]);
            try (BufferedReader reader = req.getReader()) {
                Gson gson = new Gson();
                CustomerDTO in = gson.fromJson(reader, CustomerDTO.class);
                try {
                    index = service.updateCustomer(customerId, in);
                } catch (DatabaseException e) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                } catch (DuplicateDataException | IllegalArgumentException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    return;
                } catch (NotFoundException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                    return;
                }
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter printWriter = resp.getWriter();
        printWriter.write("Successful change. Index changed:" + index);
        printWriter.close();
    }
}
