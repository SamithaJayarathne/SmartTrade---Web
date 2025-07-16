package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.List;
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

@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession httpSession = request.getSession(false);

        if (httpSession.getAttribute("email") == null) {
            responseObject.addProperty("message", "1"); // email not found
        } else {

            String email = httpSession.getAttribute("email").toString();

            JsonObject verification = gson.fromJson(request.getReader(), JsonObject.class);
            String verification_code = verification.get("code").getAsString();

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session hiberanetSession = sessionFactory.openSession();

            Criteria criteria = hiberanetSession.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("verification", verification_code));

            if (criteria.list().isEmpty()) {
                // no user found
                responseObject.addProperty("message", "Invalid verification code");
            } else {
                // verification passed
                User user = (User) criteria.list().get(0);
                user.setVerification("Verified");
                hiberanetSession.update(user);
                hiberanetSession.beginTransaction().commit();
                hiberanetSession.close();

                //store user in the session
                httpSession.setAttribute("user", user);
                //store user in the session

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Verification successful");

            }

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
