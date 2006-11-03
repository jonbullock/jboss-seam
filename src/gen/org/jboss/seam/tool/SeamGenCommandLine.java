/*******************************************************************************
 *    JBoss, Home of Professional Open Source
 *    Copyright 2006, JBoss Inc., and individual contributors as indicated
 *    by the @authors tag. See the copyright.txt in the distribution for a
 *    full listing of individual contributors.
 *   
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *   
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *   
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.jboss.seam.tool;

public class SeamGenCommandLine {

	public static void main(String[] args) throws Exception {

		if (args[0].equals("set-properties")) {
			// check to make sure we have right # of arguments
			if (args.length == 8) {
				BuildPropertiesBean projectProps = new BuildPropertiesBean(args);
				BuildPropertiesGenerator propsGen = new BuildPropertiesGenerator(
						projectProps);
				propsGen.generate();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		} 

      else if (args[0].equals("new-stateful-action")) {
         if (args.length == 4) {
            JavaClassGenerator actionGen = new JavaClassGenerator(args);            
            FaceletGenerator faceletGen = new FaceletGenerator(args);
            
            faceletGen.newFormPage();
            actionGen.newStatefulAction();            
            actionGen.newTestcase();
         } else {
            throw new Exception("Wrong number of arguments");
         }
      }

		else if (args[0].equals("new-stateless-action")) {
			if (args.length == 4) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);            
            FaceletGenerator faceletGen = new FaceletGenerator(args);
            
            faceletGen.newActionPage();
            actionGen.newStatelessAction();            
            actionGen.newTestcase();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-conversation")) {
			if (args.length == 4) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
            FaceletGenerator faceletGen = new FaceletGenerator(args);
                       
            faceletGen.newConversationPage();
				actionGen.newConversation();
            actionGen.newTestcase();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-entity")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newEntity();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-mdb")) {
			if (args.length == 5) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.setMdbDestination(args[3]);
				actionGen.setMdbDestinationType(args[4]);
				actionGen.newMdb();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		} 
		
		else {
			System.out.println("No command executed");
		}
	}

}
