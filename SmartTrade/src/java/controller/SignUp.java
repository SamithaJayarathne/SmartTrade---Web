package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First name cannot be empty");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last name cannot be empty");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password cannot be empty");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "The password must be contains"
                    + " at least uppercase, lowercase, number, special character"
                    + " and to be eight characters long");
        } else {

            //hibernate save
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {
                responseObject.addProperty("message", "User with this email already exists");
            } else {

                User u = new User();
                u.setFirst_name(firstName);
                u.setLast_name(lastName);
                u.setEmail(email);
                u.setPassword(password);

                // genarate verification code
                final String verificationCode = Util.generateCode();
                // genarate verification code

                u.setVerification(verificationCode);
                u.setCreated_at(new Date());

                session.save(u);
                session.beginTransaction().commit();
                //hibernate save

                // send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "SmartTrade - Verification", "<h1>" + verificationCode + "</h1>");
                    }
                }).start();
                // send email

            }

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Registration success. Please check your email for the verification code");

            session.close();

        }

        String responseText = gson.toJson(responseObject);

        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
