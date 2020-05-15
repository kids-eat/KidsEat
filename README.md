# KIDS EAT

## Table of Contents
1. [Overview](#Overview)
2. [Installation Guide](#Installation-Guide)
3. [Product Spec](#Product-Spec)
   * [Video Walkthrough](#Video-Walkthrough)
4. [Wireframes](#Wireframes)
5. [Schema](#Schema)
6. [License](#License)

## Overview
### Description
Kids Eat is an Android app that notifies over 4,000 low-income local community members of the locations and times of free sites organized by the non-profit Berea Kids Eat. The app also serves as a centralized platform for the organization to manage all of its events.

### App Evaluation

- **Category:**  Maps
- **Mobile:** Android Mobile Application; Uses Java, Android Studio, Firebase Firestore, Storage, Authentication, Google Maps API, and Places API.
- **Story:** Grow Appalachia is a non-profit organization located in Berea, KY, with a mission to combat food insecurity and malnutrition in the Appalachian region. Berea is home for 15,000 people, 30% of whom live below the poverty line while the national average rate for poverty is 12%. To address the problem, Grow Appalachia started the Berea Kids Eat program in a partnership with Berea College and the United States Department of Agriculture (USDA). Berea Kids Eat aims to fight childhood hunger and food insecurity within the town of Berea. The program provides free lunch and breakfast to youths aged 18 or less, especially during the summer break period when the food budget of families increases. 
- **Problem:** The organization was facing difficulties in accurately notifying families of the locations and times of their programs and meal distribution sites. Kids Eat mobile application allows Berea Kids Eat program to have a centralized platform for notifying families of the location and details of their events.
- **Market:** 30% of Berea, Kentucky population who live under the poverty line.
- **Habit:** Users can see a detailed description of events posted by Berea Kids Eat and a visual representation of nearest food locations on an interactive map. The app will also send notifications to the user notifying them of the newest events posted. 
- **Scope:** 

**First stage:**  provide the Android application to Berea Kids Eat organization and expect it to be used by nearly 4,000 people in Berea, Kentucky. 

**Second Stage:** develop an iOS version of the app.

**Third Stage:**  propose the project to United States Department of Agriculture (USDA) for it to be used across partner USDA summer meal programs nationally.

## Installation Guide
The app can be installed and tested in 5 steps:
1. Install [Android Studio](https://developer.android.com/studio) on your machine.
2. Create an [Android Virtual Device (AVD)](https://developer.android.com/studio/run/managing-avds) in your Android Studio.
3. Clone the Kids Eat repo to your local machine.
4. Open the cloned local project in Android Studio.
5. Run the app.

## Product Spec
### Video Walkthrough

Here's a walkthrough of implemented user stories in Sprint 4:

#### User View: 
<img src='https://github.com/Kids-Eat/KidsEat/blob/master/app_demos/appDemo4.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

#### Admin View: 
<img src='https://github.com/Kids-Eat/KidsEat/blob/master/app_demos/appDemo5.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).
### User Stories (Must-have and Optional)

**Required Must-have Stories**

- [x] App Authentication system allows both admin and regular users to use the same app to manage and view events.
- [x] User can sign up and create an account and by default will become a regular user, i.e. not admin.
- [x] User can sign in and out as a regular user.
- [x] User can view the list of latest 20 events posted on Kids Eat.
- [x] User can pull to refresh the latest 20 events posted on Kids Eat.
- [x] User can see details of each event in a separate activity.
- [x] The user can switch between 2 tabs: viewing list of all events (feed view) and location markers on a map for each event (map view) using fragments and a Bottom Navigation View.
- [x] User can see location markers of only upcoming events on a map, i.e. past events are automatically removed. 
- [x] Admin/Organizer can sign in and out as admin.
- [x] Admin/Organizer can view a list of events.
- [x] Admin/Organizer can create events using a form.
- [x] Admin/Organizer can upload images and search for locations using Places API.
- [x] Admin/Organizer can update previously created events. 


**Optional Nice-to-have Stories**

* User can receive push notifications in the background.
* User can turn on/off notifications.

### Screen Archetypes

* Stream
   * Create a list of events based on the data entered by Berea Kids Eat. 
   * Createa a map that shows all the events locations. 
* Detail
   *  Each event is clickable and shows its details, such as event name, location and etc.

* Settings
  * Allow users to enable/disable notifications. (BONUS)
### Navigation

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
## License

    Copyright 2020 Kids Eat

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
