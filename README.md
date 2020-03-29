# KIDS EAT

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Kids Eat is a mobile app that notifies the local community of the locations and times of free meal service events organized by the Berea Kids Eat initiative.

### App Evaluation

- **Category:**  Maps?
- **Mobile:** Android Mobile Application. Uses Google Maps API and Firebase.
- **Story:** Grow Appalachia is a non-profit organization located in Berea, KY, with a mission to combat food insecurity and malnutrition in various parts of the Appalachian region. Berea is home for 15,000 people, 30% of which live below the poverty line while the national average rate for poverty is 12%. Grow Appalachia recognized the problem and created the Berea Kids Eat program in a partnership with Berea College and the United States Department of Agriculture (USDA). Berea Kids Eat, one of the many initiatives of Grow Appalachia, is directed to fight childhood hunger and food insecurity within the town of Berea. The program provides free lunch and breakfast to youths aged 18 or less, especially during the summer break period when the food budget of families increases. However, they have been having problems with notifying families of the locations of the programs they have. Kids Eat mobile application will allows Berea Kids Eat program to have a centralized platform for notifying families of the location and details of their events.
- **Market:** 30% of Berea, Kentucky population who live under the poverty line.
- **Habit:** Users can see a detailed description of events posted by Berea Kids Eat and a visual representation of nearest food locations on an interactive map. The app will also send notifications to the user notifying them of the newest events posted. 
- **Scope:** For the first stage we will provide this application to 15,000 population of Berea, Kentucky. Later, we plan to propose the project to United States of Agriculture for it to be used nationally. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* A recycler-view for showing all the events that has been posted by the Berea Kids Eat organizers. 
* Detail activity for showing the name description, meal-type, and time and date of each event. 
* A map giving a visual picture of all the events locations.

**Optional Nice-to-have Stories**

* A notification system that notifies users of the most current events added by the Berea Kids Eat team members. 
* Users can turn on/off notifications.

### 2. Screen Archetypes

* Stream
   * Create a list of events based on the data entered by Berea Kids Eat. 
   * Createa a map that shows all the events locations. 
* Detail
   *  Each event is clickable and shows its details, such as event name, location and etc.

* Settings
  * Allow users to enable/disable notifications. (BONUS)
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Tab Navigation
* Flow Navigation

**Flow Navigation** (Screen to Screen)

* Tab Navigation 
   * Events list 
   * Map
* Flow Navigation
   * Events list
     => Navigation to Event Details

## Wireframes
![](https://i.imgur.com/CKhi2as.png)


## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | name          | String   | name of the event |
   | description   | String   | event details |
   | image         | File     | image of event that organizer posts |
   | date          | String   | date when the event will take place |
   | time          | String   | time when the event will take place |
   | address       | String   | address of the event |
   | meal-type     | String   | the type of meal provided at the event |
   | createdAt     | Timestamp| date when event is created |
   | latlng        | Geopoint | latitude and longitude of event location |

### Networking
#### List of network requests by screen

 - List View
  - (Read/GET) Query all events
    ```db.collection("events")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<String> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        events.add(doc.getString("name"));
                    }
                }
            }
        });```
  - Detail Activity Screen
    - (Read/GET) Query all known attributes of the selected event
      ```
      DocumentReference docRef = db.collection("events").document(eventId);
      docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()) {
                  DocumentSnapshot document = task.getResult();
                  if (document.exists()) {
                      Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                  } else {
                      Log.d(TAG, "No such document");
                  }
              } else {
                  Log.d(TAG, "get failed with ", task.getException());
              }
          }
      });

      ```
  - Map View
    - (Read/GET) Query latlng for all the events in the database
      ```
      db.collection("events")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<String> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        events.add(doc.getString("name"));
                    }
                }
            }
        });
      ```
  - Organizer View
    - (Create/POST) Create a new event as a document in Cloud Firestore
      ```Map<String, Object> data = new HashMap<>();
          data.put(key, value)

          db.collection("events")
                  .add(data)
                  .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                      @Override
                      public void onSuccess(DocumentReference documentReference) {
                          Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Log.w(TAG, "Error adding document", e);
                      }
        });
      ```
    - (Update/PUT) Update information about an event
      ```
         DocumentReference washingtonRef = db.collection("events").document(eventId);
         washingtonRef
        .update("time", true)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
        ```
    - (Delete) Delete existing event
      ```
         db.collection("events").document(eventId)
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
      ```
