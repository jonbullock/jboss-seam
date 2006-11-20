//$Id: ChangePasswordTest.java,v 1.13 2006/10/26 21:10:41 gavin Exp $
package org.jboss.seam.example.booking.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class ChangePasswordTest extends SeamTest
{
   
   @Test
   public void testChangePassword() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }
         
      }.run();
      
      new FacesRequest() {
         
         @Override
         protected void processValidations() throws Exception
         {
            validateValue("#{user.password}", "xxx");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxyyyx");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{changePassword.changePassword}")==null;
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);
         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxxyyy");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{changePassword.changePassword}").equals("main");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("xxxyyy");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            assert getValue("#{user.password}").equals("xxxyyy");
            setValue("#{user.password}", "foobar");
            setValue("#{changePassword.verify}", "foobar");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{changePassword.changePassword}").equals("main");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
   }

}
