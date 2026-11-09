package com.vehiclerental;

import com.vehiclerental.model.*;
import com.vehiclerental.service.*;
import com.vehiclerental.config.FeedbackWebConfig;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import com.vehiclerental.controller.FeedbackController;
import com.vehiclerental.portal.controller.CustomerFeedbackController;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.lang.reflect.*;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class FeedbackSessionIntegrationTest {
 static class Registry extends InterceptorRegistry { Object[] configured() { return getInterceptors().toArray(); } }
 static int passed; static Feedback saved; static List<Feedback> items=new ArrayList<>();
 static void check(boolean b,String name){if(!b)throw new AssertionError(name);System.out.println("PASS: "+name);passed++;}
 static MockHttpSession session(User u,long id)throws Exception{Field f=User.class.getDeclaredField("userId");f.setAccessible(true);f.set(u,id);var s=new MockHttpSession();s.setAttribute("loggedInUserId",id);s.setAttribute("loggedInRole",u.getRole().name());return s;}
 static Feedback item(long customer){var f=new Feedback();f.setCustomerId(customer);f.setRating(1);f.setFeedbackType(FeedbackType.COMPLAINT);f.setSubject("Pickup");f.setMessage("Late pickup");return f;}
 static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder valid(){return post("/customer/feedback").param("feedbackType","COMPLAINT").param("rating","1").param("subject","  Pickup  ").param("message","  Arrived late  ");}
 static Map<?,?> errors(MvcResult r){return (Map<?,?>)r.getModelAndView().getModel().get("errors");}
 @org.junit.jupiter.api.Test
 void existingLoginSessionProtectsFeedbackRoutes() throws Exception {
  FeedbackService svc=(FeedbackService)Proxy.newProxyInstance(FeedbackService.class.getClassLoader(),new Class[]{FeedbackService.class},(p,m,a)->switch(m.getName()){
   case "search"->items;case "bookingBelongsToCustomer"->Long.valueOf(1).equals(a[0])&&Long.valueOf(10).equals(a[1]);
   case "submit"->{saved=(Feedback)a[0];saved.setStatus(FeedbackStatus.OPEN);saved.setAdminResponse(null);yield saved;}
   case "getCustomerNames"->Map.of();case "getCustomerOptions","getBookingOptions"->List.of();case "findById"->Optional.empty();default->null;});
  var validator=new LocalValidatorFactoryBean();validator.afterPropertiesSet();
  var registry=new Registry();new FeedbackWebConfig().addInterceptors(registry);
  var configured=registry.configured();
  var customer=MockMvcBuilders.standaloneSetup(new CustomerFeedbackController(svc)).setValidator(validator).addInterceptors(((org.springframework.web.servlet.handler.MappedInterceptor)configured[0]).getInterceptor()).build();
  var admin=MockMvcBuilders.standaloneSetup(new FeedbackController(svc)).setValidator(validator).addInterceptors(((org.springframework.web.servlet.handler.MappedInterceptor)configured[1]).getInterceptor()).build();
  var nimal=session(new Customer(),10);var driver=session(new Driver(),20);var administrator=session(new Admin(),30);
  check(customer.perform(get("/customer/feedback")).andReturn().getResponse().getRedirectedUrl().equals("/login"),"Anonymous customer page redirects to login");
  check(customer.perform(get("/customer/feedback").session(driver)).andReturn().getResponse().getRedirectedUrl().equals("/drivers"),"Driver cannot access customer feedback");
  check(admin.perform(get("/feedback").session(nimal)).andReturn().getResponse().getRedirectedUrl().equals("/customer/feedback"),"Customer Feedback navbar link opens personal feedback");
  check(admin.perform(get("/feedback").session(administrator)).andReturn().getModelAndView().getViewName().equals("feedback/list"),"Admin can access feedback management");
  items=List.of(item(10),item(11));var mine=customer.perform(get("/customer/feedback").session(nimal)).andReturn();
  check(((List<?>)mine.getModelAndView().getModel().get("feedbackList")).size()==1,"My feedback hides other customers' feedback");
  check(customer.perform(get("/customer/feedback/new").param("rideId","2").session(nimal)).andReturn().getResponse().getRedirectedUrl().equals("/customer/feedback"),"GET rejects another customer's booking");
  check(customer.perform(get("/customer/feedback/new").param("rideId","1").session(nimal)).andReturn().getModelAndView().getViewName().equals("portal/feedback-form"),"GET accepts own booking");
  var wrong=customer.perform(valid().param("bookingId","2").session(nimal)).andReturn();check(errors(wrong).containsKey("bookingId"),"POST rejects another customer's booking");
  var empty=customer.perform(post("/customer/feedback").param("subject","").param("message","").session(nimal)).andReturn();
  check(errors(empty).containsKey("subject")&&errors(empty).containsKey("message"),"Blank subject and message show validation errors");
  var bad=customer.perform(post("/customer/feedback").param("subject","x").param("message","x").param("rating","6").session(nimal)).andReturn();
  check(errors(bad).containsKey("rating"),"Out-of-range rating rejected");
  var invalid=customer.perform(post("/customer/feedback").param("subject","x").param("message","x").param("feedbackType","INVALID").session(nimal)).andReturn();check(errors(invalid).containsKey("feedbackType"),"Invalid feedback type rejected");
  var submit=customer.perform(valid().param("bookingId","1").param("customerId","11").param("status","RESOLVED").param("adminResponse","Fake reply").session(nimal)).andReturn();
  check(submit.getResponse().getRedirectedUrl().equals("/customer/feedback"),"Valid complaint redirects to My feedback");
  check(saved.getCustomerId().equals(10L),"Forged customer id ignored; session identifies customer");
  check(saved.getStatus()==FeedbackStatus.OPEN&&saved.getAdminResponse()==null,"Customer cannot forge status or admin reply");
  check(saved.getSubject().equals("Pickup")&&saved.getMessage().equals("Arrived late"),"Submitted text is trimmed");
  check(saved.getPriority().equals("HIGH"),"One-star complaint uses HIGH priority");
  saved.setRating(5);check(saved.getPriority().equals("MEDIUM"),"Five-star complaint uses MEDIUM priority");
  saved.setFeedbackType(FeedbackType.GENERAL);check(saved.getPriority().equals("LOW"),"Five-star general feedback uses LOW priority");
  saved.setRating(1);check(saved.getPriority().equals("MEDIUM"),"One-star general feedback uses MEDIUM priority");
  var resolve=admin.perform(post("/feedback/1").session(administrator).param("customerId","10").param("feedbackType","COMPLAINT").param("rating","1").param("subject","Pickup").param("message","Late").param("status","RESOLVED").param("adminResponse","")).andReturn();
  check(errors(resolve).containsKey("adminResponse"),"Complaint cannot be resolved without a reply");
  validator.close();System.out.println("All "+passed+" checks passed. Database and browser rendering were not tested.");
 }
}
