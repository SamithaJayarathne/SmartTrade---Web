package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signInJson = gson.fromJson(request.getReader(), JsonObject.class);

        String email = signInJson.get("email").getAsString();
        String password = signInJson.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password cannot be empty");
        } else {

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session hibernateSession = sessionFactory.openSession();

            Criteria criteria = hibernateSession.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("password", password));

            if (criteria.list().isEmpty()) {
                // no user with these credentials
                responseObject.addProperty("message", "No user found with this details. "
                        + "Please check the details and try again");

            } else {
                // user found
                User user = (User) criteria.list().get(0);
                responseObject.addProperty("status", true); // login successful
                HttpSession httpSession = request.getSession();

                if (!user.getVerification().equalsIgnoreCase("Verified")) {
                    // not verified. Need to redirect to the verification page

                    // set email to the session
                    httpSession.setAttribute("email", email);
                    // set email to the session

                    responseObject.addProperty("message", "1"); // not verified

                } else {

                    httpSession.setAttribute("user", user);
                    responseObject.addProperty("message", "2"); // verified

                }

            }

            hibernateSession.close();

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        HttpSession httpSession = request.getSession();
//
//        JsonObject responseObject = new JsonObject();
//
//        if (httpSession.getAttribute("user") != null) {
//
////            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
////            Session hibernateSession = sessionFactory.openSession();
////
////            Criteria criteria = hibernateSession.createCriteria(User.class);
////            
////            String email = httpSession.getAttribute("email").toString();
////            
////            criteria.add(Restrictions.eq("email", request));
////            
////            if(criteria.list().isEmpty()){
////            
////            }
//
//
//
//            responseObject.addProperty("status", true);
//
//        } else {
//            responseObject.addProperty("status", false);
//        }
//
//        Gson gson = new Gson();
//        String responseText = gson.toJson(responseObject);
//        response.setContentType("application/json");
//        response.getWriter().write(responseText);
//
//    }

}
