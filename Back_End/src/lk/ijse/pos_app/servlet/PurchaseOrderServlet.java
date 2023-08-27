package lk.ijse.pos_app.servlet;

import lk.ijse.pos_app.dto.CustomerDTO;
import lk.ijse.pos_app.dto.ItemDTO;
import lk.ijse.pos_app.dto.PurchaseOrderDTO;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/pages/purchase-order")
public class PurchaseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajax", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("SELECT code FROM item");
            ResultSet resultSet = pstm.executeQuery();

            resp.addHeader("Access-Control-Allow-Origin","*");

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            ItemDTO itemDTO = new ItemDTO();

            while (resultSet.next()){
                itemDTO.setCode(resultSet.getString(1));
                JsonObjectBuilder code = Json.createObjectBuilder();
                code.add("code",itemDTO.getCode());

                arrayBuilder.add(code.build());
            }

            PreparedStatement cusPstm = connection.prepareStatement("SELECT id FROM customer2");
            ResultSet cusDetails = cusPstm.executeQuery();

            CustomerDTO customerDTO = new CustomerDTO();
            while (cusDetails.next()){
                customerDTO.setId(cusDetails.getString(1));
                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("id",customerDTO.getId());

                arrayBuilder.add(customer.build());
            }

            resp.getWriter().print(arrayBuilder.build());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String oId = jsonObject.getString("oId");
        int qty = Integer.parseInt(jsonObject.getString("orderQty"));
        double cash = Double.parseDouble(jsonObject.getString("cash"));
        double balance = Double.parseDouble(jsonObject.getString("balance"));
        Date date = Date.valueOf(jsonObject.getString("date"));
        String cusId = jsonObject.getString("cusId");
        String itemCode = jsonObject.getString("itemCode");

        PurchaseOrderDTO orderDTO = new PurchaseOrderDTO(oId,qty,cash,balance,date,cusId,itemCode);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajax", "root", "1234");

            connection.setAutoCommit(false);

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO orderDetails VALUES (?,?,?,?,?,?,?)");
            pstm.setObject(1,orderDTO.getoId());
            pstm.setObject(2,orderDTO.getOrderQty());
            pstm.setObject(3,orderDTO.getCash());
            pstm.setObject(4,orderDTO.getBalance());
            pstm.setObject(7,orderDTO.getDate());
            pstm.setObject(5,orderDTO.getCusId());
            pstm.setObject(6,orderDTO.getItemCode());

            if (pstm.executeUpdate() > 0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT qty FROM item WHERE code=?");
                preparedStatement.setObject(1,orderDTO.getItemCode());
                ResultSet resultSet = preparedStatement.executeQuery();

                int qtyOnHand = resultSet.getInt(1);
                int newValue = qtyOnHand-orderDTO.getOrderQty();

                PreparedStatement preparedStatement1 = connection.prepareStatement("UPDATE item SET qty=? WHERE code=?");
                preparedStatement1.setObject(1,newValue);
                preparedStatement1.setObject(2,orderDTO.getItemCode());

                if (preparedStatement1.executeUpdate() > 0){
                    connection.commit();

                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                    objectBuilder.add("state", "OK");
                    objectBuilder.add("message", "Successfully Order Added.....");
                    objectBuilder.add("Data", " ");
                    resp.getWriter().print(objectBuilder.build());

                    connection.setAutoCommit(true);
                    return;
                }
            }

            connection.rollback();
            connection.setAutoCommit(true);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String id = jsonObject.getString("id");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajax", "root", "1234");

            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(id);

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer2 WHERE id=?");
            pstm.setObject(1,customerDTO.getId());

            ResultSet resultSet = pstm.executeQuery();

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            while (resultSet.next()){
                String id1 = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                String salary = String.valueOf(resultSet.getDouble(4));

                objectBuilder.add("id",id1);
                objectBuilder.add("name",name);
                objectBuilder.add("address",address);
                objectBuilder.add("salary",salary);
            }

            resp.getWriter().print(objectBuilder.build());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        String code = jsonObject.getString("code");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajax", "root", "1234");

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setCode(code);

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setObject(1,itemDTO.getCode());

            ResultSet resultSet = pstm.executeQuery();

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            while (resultSet.next()){
                String code1 = resultSet.getString(1);
                String name = resultSet.getString(2);
                String qty = String.valueOf(resultSet.getInt(3));
                String price = String.valueOf(resultSet.getDouble(4));

                objectBuilder.add("code",code1);
                objectBuilder.add("name",name);
                objectBuilder.add("qty",qty);
                objectBuilder.add("price",price);
            }

            resp.getWriter().print(objectBuilder.build());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","PUT, DELETE");
        resp.addHeader("Access-Control-Allow-Headers","content-type");
    }
}
