<%@ page import="hk.edu.ouhk.lib.cat.*,hk.edu.ouhk.lib.*,hk.edu.ouhk.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*,javax.mail.*,javax.mail.internet.*,javax.activation.*"%>
<%
	try {
		out.println("HELLO");
 // Recipient's email ID needs to be mentioned.
      String to = "wwyng@ouhk.edu.hk";

      // Sender's email ID needs to be mentioned
      String from = "cpcat@cpcat.ouhk.edu.hk";

      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      javax.mail.Session session = javax.mail.Session.getDefaultInstance(properties);

    // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject("This is the Subject Line!");

         // Now set the actual message
         message.setText("This is actual message");

         // Send message
         Transport.send(message);
         out.println("Sent message successfully....");

	} //end try

	catch (Exception e) {
                out.println("<br>");
                out.println("<br>");
                e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
