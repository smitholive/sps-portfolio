// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['My favorite distance running race is 13.1 miles, aka a half marathon.', 'My favorite color is yellow.', 'I collect japanese plush alpacas called Alpacassos.', 'I board dogs semi-professionally.'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Loads in comment data
 * fetch returns a promise which becomes a stream when served
 * we then use the text function (which is another promise since text()
 * can take a little while) to turn that stream into HTML text
 * which we then place into the appropriate HTML container
 */
 function getComments(){
    fetch('/data').then(response => response.json()).then((commentsJson) => {
      const commentsElement = document.getElementById('comment-container');
      commentsElement.innerHTML = '';

      // iterate through all the comment JSON objects and
      // format them correctly
      for(var i = 0; i < commentsJson.length; i++){
        var comment = commentsJson[i];
        commentsElement.innerHTML += comment.name + ' at ' 
        + comment.timestamp 
        + ' said: ' + comment.message + '<br>';
      }
    })
 }
