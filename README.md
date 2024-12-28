# Automation-Of-Residental-And-Business-Complexes

The ARBC (Automation Of Residential And Business Complexes) application is a comprehensive management system designed to streamline and automate the operations of residential and business complexes. Application itself is built with modularity, scalability, and ease of use in mind, aiming to provide an all-encompassing solution for modern complex management. It is ideal for residential and business complexes looking to harness the power of IoT and automation.

**This project is also part of my computer science bachelors thesis :D**

## Technologies Used

- Java 17
- Wildfly application server 33.0
- JAX-RS (RESTEasy) framework
- Mosquitto 2.0
- MongoDB 7.0
- RabbitMQ 4.0
- JQuery 3.6
- HTML 5
- CSS 3 (Bootstrap)

## ARBC database collection schemas

Schemas for the existing database collections are shown in the pictures inside this repository.

## Overview

Since the application modules are not hosted anywhere due insufficient budget for now, below you can find links to video demonstrations of application usages for different user groups:

- [Admin](https://drive.google.com/file/d/1unvaTxoBaS9rlNV_rG0BFf5IBgzltXoM/view?usp=sharing)
- [Sports and wellness center worker](https://drive.google.com/file/d/1-_cXRvrQtwb-dHDZkT0FF5knzGKEkRCx/view?usp=sharing)
- [Resident](https://drive.google.com/file/d/1dNrGjRoUALdZYkQo2aasyfJu4ntEfnbo/view?usp=sharing)

ARBC system is divided into 4 different modules:

- User management
- Residence management
- Sports and wellness center
- Notificaitons

When it comes to frontend, there is a page for each use case that is mentioned in the below text.

ARBC system features 3 group of users as mentioned above and each one of them can do the following:

### Admin

**CRUD operations on residents**
 To create a resident account in the system, the following data must be entered:
- Email – Must follow this regex pattern: ^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$
- First Name – Can contain only English alphabet letters and spaces.
- Last Name – Can contain only English alphabet letters and spaces.

The submitted data is validated on the server, and if it is invalid, the administrator receives feedback.

The account creation process on the server first checks if an account with the submitted email address already exists. If it does, an error message is returned, as the email address must be unique in the system.

Once validated, a random password is generated for the resident and sent to their email along with other details after the account is successfully created. The data is sent as a JSON object to a specific queue so that the notification service can pick it up, process it, and send an email to the provided address. This email notifies the resident that their account has been created and provides their login credentials.

The search for residents is performed by matching the letters in the search query with the letters in the resident's email. Specifically, if all the letters in the search query are found within the email, the resident is included in the results. This process is handled on the server by simply fetching the relevant list of residents.

Each row in the table, in addition to displaying the resident's information, also contains buttons that allow the following actions:

- Deleting the resident's account
- Resetting the resident's password
- Blocking the resident from making reservations for the resources of the sports and recreational center

Deleting resident accounts through the user interface is very intuitive and requires only a click on the delete button. When generating table rows, the ID of each resident displayed in the interface is dynamically added to the HTML tag. This ID is included in the HTTP request sent for deleting the resident account.

The system first checks whether a resident exists for the provided ID. If the resident does not exist, the administrator is notified. If the resident does exist, the system contacts the residency management service to verify if the resident is linked to any housing unit. If such a link exists, the resident cannot be deleted.

If the resident is not associated with any housing unit, they are removed from the system. A message containing the necessary details is then sent to a specific channel within the RabbitMQ broker. The notification service reads the notification from RabbitMQ and sends an email to the resident informing them that their account has been deleted.





  
