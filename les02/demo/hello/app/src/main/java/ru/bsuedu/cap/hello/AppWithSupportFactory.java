/*
 * This source file was generated by the Gradle 'init' task
 */
package ru.bsuedu.cap.hello;


public class AppWithSupportFactory {
    public static void main(String[] args) {
        MessageSupportFactory factory = MessageSupportFactory.getInstance();
        MessageProvider messageProvider = factory.getMessageProvider();
        MessageRenderer messageRenderer = factory.getMessageRenderer();
        messageRenderer.setMessageProvider(messageProvider);
        messageRenderer.render();
     }
}
