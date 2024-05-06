document.addEventListener('DOMContentLoaded', function() {
    // Initially hide the guessed words display and header
    initializeGuessedWordsDisplay();

    document.getElementById('guessButton').addEventListener('click', submitGuess);
    document.getElementById('newGameButton').addEventListener('click', newGame);
    document.getElementById('guessInput').addEventListener('keyup', function(event) {
        if (event.key === "Enter") {
            submitGuess();
        }
    });
});

function initializeGuessedWordsDisplay() {
    let previousGuessesContainer = document.getElementById('previousGuesses');
    let previousGuessesHeader = document.querySelector('.previous-guesses h2');
    previousGuessesHeader.style.visibility = 'hidden';
    previousGuessesContainer.style.visibility = 'hidden';
    previousGuessesContainer.innerHTML = ''; // Clear any existing content
}

function submitGuess() {
    let word = document.getElementById('guessInput').value;
    if (word.length !== 5) {
        document.getElementById('message').textContent = "Please enter a 5-letter word.";
        return;
    }

    fetch(`http://localhost:8080/guess?word=${word}`)
    .then(response => response.json())
    .then(data => {
        if (data.errorMessage) {
            document.getElementById('message').textContent = data.errorMessage;
        } else {
            document.getElementById('message').textContent = data.message;
        }
        displayResults(data);
        updatePreviousGuessesDisplay(data.guessedWords);
        document.getElementById('guessInput').value = ''; // Clear input after each guess
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('message').textContent = 'Server connection error.';
    });
}

function newGame() {
    fetch('http://localhost:8080/newgame')
    .then(() => {
        document.getElementById('wordleGrid').innerHTML = '';
        initializeGuessedWordsDisplay();
        document.getElementById('message').textContent = 'New game started. Good luck!';
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('message').textContent = 'Server connection error.';
    });
}

function displayResults(data) {
    let grid = document.getElementById('wordleGrid');
    grid.innerHTML = '';
    if (data.results) {
        data.results.forEach(result => {
            let cell = document.createElement('div');
            cell.className = 'wordleCell ' + result.toLowerCase();
            grid.appendChild(cell);
        });
    }
}

function updatePreviousGuessesDisplay(guessedWords) {
    let previousGuessesContainer = document.getElementById('previousGuesses');
    let previousGuessesHeader = document.querySelector('.previous-guesses h2');
    previousGuessesContainer.innerHTML = ''; // Clear existing content

    if (guessedWords.length > 0) {
        guessedWords.forEach(word => {
            let cell = document.createElement('div');
            cell.className = 'previousGuessCell';
            cell.textContent = word;
            previousGuessesContainer.appendChild(cell);
        });
        previousGuessesHeader.style.visibility = 'visible'; // Show the header if there are guesses
        previousGuessesContainer.style.visibility = 'visible'; // Show the container if there are guesses
    } else {
        previousGuessesHeader.style.visibility = 'hidden'; // Hide the header if there are no guesses
        previousGuessesContainer.style.visibility = 'hidden'; // Hide the container if there are no guesses
    }
}
