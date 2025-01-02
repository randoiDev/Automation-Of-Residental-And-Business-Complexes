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

Resetting a resident's password is sometimes a necessary operation if the resident forgets their login credentials. The safest option is for the administrator to handle the password reset, as securing access to the resident's devices is critical. By clicking a button, the resident's ID is included in the HTTP request to reset the password.

The final operation that the administrator can perform regarding residents is placing a veto to prevent residents from making reservations for the usage of sports and recreational center resources. The same goes as for deletion, the resident's ID is included in the HTTP request to reset the password.

**CRUD operations on workers**

For workers, the operations are implemented almost identically to those for residents, both on the server and in the user interface, with a few exceptions:

- Account Creation – When creating an account for a sports center worker, the same fields are filled out as when creating a resident account. However, there is an additional field for the mobile phone number. The specified email is not stored in the collection; it is used only to send a message to the new worker, informing them that their account has been created. In addition to the password, a random username is also generated in the same way and format as the password. Error messages for validation issues are displayed in the same manner.

- Account Deletion – The only difference is that no email is sent when deleting an account. As mentioned, admin users cannot delete each other through this user interface since they are also workers.

- Account Listing – Account listing works similarly, with the exception of filtering, as accounts are now filtered by username. The only available action button is for account deletion, as there are no operations for placing vetoes or resetting passwords.

**Attaching/Detaching residences to/from residents**

For a resident to manage devices within a residential unit, they must first be the owner of that residence in the collection of existing residences within a complex. Specifically, their email must be listed in the document representing a residence in the collection. To assign a resident as the owner of a residence in the database, the admin user must use the panel shown in the demonstration video.

From the given panel, the admin selects a resident from the list of resident emails and assigns them one or more residences selected from the residence selection menu above. First, an HTTP request is sent to the user management module to verify that a resident exists in the system for the provided email. Then, the list of submitted residence numbers is processed to check if each residence exists and whether it is occupied. If no residence exists for a given number or it is already occupied, an appropriate response is returned to the admin.

Subsequently, a bulk-write operation is used to atomically modify all the documents (i.e., residences) in the collection or none at all. After the operation, a confirmation email is sent to the resident listing the residences they now own in the system.

The process for revoking a resident's ownership of residences in the system is similar to the assignment process, except that the documents representing the residences are modified to set the residents_email field to null, thereby removing ownership. The email sent in this case follows the same structure but informs the resident that their ownership of the residences has been revoked.

**CRUD operations on sports and wellness center resource reservation appointments**

The term "Reservation appointment" in the ARBC system represents a time period during which a specific resource of the sports and recreational center can be used on a given date. Currently, the sports and recreational center is designed to include only the pool, gym, and sauna as resources.

The creation of these appointments is the responsibility of the admin user, and the panel used for it is shown in the video. To create an appointment, the following information is required:

- Resource – At the moment, it is only possible to create appointments for the sauna and gym,
- Appointment start time – The date and time when the appointment begins, which must be at least 24 hours ahead of the current time,
- Appointment end time – The date and time when the appointment ends, which must be chronologically after the start time, with a minimum duration of 1 hour, and
- Maximum number of reservations – The maximum number of reservations allowed for the appointment, which must have a minimum value of 3.

All the aforementioned constraints are validated on the server side when the reservation appointment creation request is submitted.

On the right side of this panel in the user interface, there is a table displaying existing appointments. Deleting appointment is not permitted if less than 4 hours remain before the start of the slot. After the start time, deleting  is allowed.

If a appointment has reservations at the time it is deleted by the admin, all residents with reservations in that slot will receive an email notification about the cancellation. An example of this email is shown in the demonstration video of resident users.

### Sports and wellness center worker

**Manipulating IoT devices in the center**

As shown in demonstration video, following parameters can be changed:

- Sauna
  - Heater temperature 25-35°C
  - Lights 0-5 volume
- Gym
  - Heater temperature 25-35°C
  - Lights 0-5°C
  - Air conditioner 15-25°C
- Swimming pool
  - Heater temperature 25-35°C
  - Exterior lights 0-5 volume
  - Changing color lights in pool:
    - Blue
    - Red
    - Green
    - Purple
    - No color
  - Opening/Closing swimming pool roof
  - Filling/Emptying swimming pool

 **Checking in arrivals on reservations**

 If resident didn't show up by time the reservation appointment starts, sports and wellness center worker can mark that resident did not arrive on his/her reservation.

 ### Resident

 **Manipulating IoT devices in the apartment**

 As shown in the demonstration video, following parameters can be changed:

 - Heater temperature 25-35°C
 - Opening/Closing windows in different rooms in the apartment
 - Air conditioner 15-25°C

**Creating reservations**

In the video we can see the interface where residents can make reservations for existing appointments with a simple click of the "Create" button and view all their created reservations in the table on the right. Residents can also delete a reservation if necessary.

On the server side, various limitations must be satisfied for a reservation to be created:

- The resident must not have a veto on creating reservations,
- The resident must own at least one residence, which is verified by contacting the residence management module,
- Reservations cannot be made less than 4 hours before the appointment begins, and
- The allowed number of reservations must not be exceeded.

If the reservation is successfully created, an email is sent to the resident with details about the reservation appointment and a reservation number that uniquely identifies the owner of the reservation.

As for the process of deleting a reservation, it has several limitations:

- Verify that an appointment exists for the provided appointment ID,
- Verify that a reservation exists for the provided reservation number and that the resident initiating the deletion is the owner of that reservation, and
- The reservation cannot be deleted if less than 4 hours remain before it begins.

## Future Enhancements

As the project progresses, planned upgrades will further enhance the system and enable the implementation of new functionalities. These upgrades will include:

- The ability to add devices we want to control via the ARBC application instead of relying on predefined ones,
- Controlling a wider range of parameters for IoT devices,
- Higher levels of security when accessing devices on the network,
- System-level vetoing of resident reservations for sports and recreational center resource reservation appointments,
- New user types and modules (e.g., security workers and a security system module, cinema workers and a cinema module, etc.),
- Personalized recommendations for parameter settings,
- New types of residences, such as houses, etc.

## License

This project is open source and is released under the [Creative Commons Zero License](https://creativecommons.org/publicdomain/zero/1.0/).

You are free to use, modify, and distribute this software for any purpose without any restrictions. Please refer to the [LICENSE](LICENSE) file for more details.

