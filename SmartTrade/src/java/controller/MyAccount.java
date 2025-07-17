package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "MyAccount", urlPatterns = {"/MyAccount"})
public class MyAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);

        if (httpSession != null && httpSession.getAttribute("user") != null) {
            User user = (User) httpSession.getAttribute("user");

            JsonObject responseObject = new JsonObject();

            String first_name = user.getFirst_name();
            String last_name = user.getLast_name();
            String password = user.getPassword();

            String since = new SimpleDateFormat("MMM yyyy").format(user.getCreated_at());

            responseObject.addProperty("fname", first_name);
            responseObject.addProperty("lname", last_name);
            responseObject.addProperty("password", password);
            responseObject.addProperty("since", since);

            Gson gson = new Gson();
            String responseText = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(responseText);
        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String fname = userData.get("fname").getAsString();
        String lname = userData.get("fname").getAsString();
        String currentPassword = userData.get("currentPassword").getAsString();
        String newPassword = userData.get("newPassword").getAsString();
        String confirmNewPassword = userData.get("confirmNewPassword").getAsString();
        int cityId = userData.get("citySelect").getAsInt();
        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        String postalCode = userData.get("postalCode").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        // validate fields
        // validate fields
        HttpSession httpSession = request.getSession();

        if (httpSession != null && httpSession.getAttribute("user") != null) {

            User user = (User) httpSession.getAttribute("user");

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session hibernateSession = sessionFactory.openSession();

            Criteria c = hibernateSession.createCriteria(User.class);
            c.add(Restrictions.eq("email", user.getEmail()));

            if (!c.list().isEmpty()) {
                User dbUser = (User) c.list().get(0);

                dbUser.setFirst_name(fname);
                dbUser.setLast_name(lname);

                if (!confirmNewPassword.isEmpty()) {
                    dbUser.setPassword(confirmNewPassword);
                } else {
                    dbUser.setPassword(currentPassword);
                }

                City city = (City) hibernateSession.load(City.class, cityId);

                Address address = new Address();
                address.setLine_1(lineOne);
                address.setLine_2(lineTwo);
                address.setPostal_code(postalCode);
                address.setCity(city);
                address.setUser(dbUser);

                //also update the user object stored in session
                httpSession.setAttribute("user", dbUser);
                //also update the user object stored in session

                hibernateSession.merge(dbUser);
                hibernateSession.save(address);

                hibernateSession.beginTransaction().commit();
                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("message", "User profile updated successfully");
                hibernateSession.close();
                

            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
