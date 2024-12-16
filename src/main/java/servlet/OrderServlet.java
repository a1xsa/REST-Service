package servlet;

import DTO.CustomerDTO;
import DTO.OrderDTO;
import com.google.gson.Gson;
import exception.DatabaseException;
import exception.DuplicateDataException;
import exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Order;
import service.CustomerService;
import service.OrderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    private OrderService service;

    @Override
    public void init() {
        service = new OrderService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<OrderDTO> result = new ArrayList<>();
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
                Integer orderId = Integer.parseInt(pathInfo[0]);
                try {
                    result = service.getById(orderId);
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
        String id = req.getParameter("customer_id");
        if (id == null || id.isEmpty() || !id.matches("\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required for add orders");
            return;
        }
        Integer id_customer = Integer.parseInt(id);
        try (BufferedReader reader = req.getReader()) {
            Gson gson = new Gson();
            OrderDTO in = gson.fromJson(reader, OrderDTO.class);
            try {
                index = service.add(in, id_customer);
            } catch (DatabaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            } catch (NotFoundException | IllegalArgumentException e) {
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is required for deletion");
            return;
        }
        String[] pathInfo = req.getPathInfo().substring(1).split("/");
        if (pathInfo.length == 1 && pathInfo[0].matches("\\d+")) {
            Integer orderId = Integer.parseInt(pathInfo[0]);
            try {
                index = service.deleteById(orderId);
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is required for update");
            return;
        }
        String[] pathInfo = req.getPathInfo().substring(1).split("/");
        if (pathInfo.length == 1 && pathInfo[0].matches("\\d+")) {
            Integer orderId = Integer.parseInt(pathInfo[0]);
            try (BufferedReader reader = req.getReader()) {
                Gson gson = new Gson();
                OrderDTO in = gson.fromJson(reader, OrderDTO.class);
                try {
                    index = service.updateOrder(orderId, in);
                } catch (DatabaseException e) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                } catch (IllegalArgumentException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    return;
                } catch (NotFoundException e){
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
