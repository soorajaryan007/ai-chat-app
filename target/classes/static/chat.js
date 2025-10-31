// Wait for the page to load
document.addEventListener('DOMContentLoaded', () => {
    
    // --- NEW ---
    // Get the conversation ID from the <body> tag's data attribute
    const conversationId = document.body.dataset.conversationId;
    if (!conversationId) {
        console.error("Conversation ID is missing!");
        return;
    }
    
    // Get all the elements we need
    const chatForm = document.getElementById('chat-form');
    const questionInput = document.getElementById('question-input');
    const chatHistory = document.getElementById('chat-history');
    const submitButton = chatForm.querySelector('button');

    // Utility function to scroll to the bottom
    function scrollToBottom() {
        chatHistory.scrollTop = chatHistory.scrollHeight;
    }

    // Scroll to bottom on page load
    scrollToBottom();

    // Utility function to add a new message to the UI
    function addMessageEntry(type, text, timestamp) {
        const entry = document.createElement('div');
        entry.className = 'chat-entry';

        const p = document.createElement('p');
        p.className = (type === 'user') ? 'question' : 'answer';

        const strong = document.createElement('strong');
        strong.textContent = (type === 'user') ? 'You: ' : 'AI: ';
        
        const span = document.createElement('span');
        span.textContent = text;
        
        p.appendChild(strong);
        p.appendChild(span);
        entry.appendChild(p);

        if (timestamp) {
            const time = document.createElement('span');
            time.className = 'timestamp';
            time.textContent = new Date(timestamp).toLocaleTimeString();
            entry.appendChild(time);
        }
        
        chatHistory.appendChild(entry);
    }
    
    // Utility for loading indicator
    function showLoading() {
        const loading = document.createElement('div');
        loading.id = 'loading';
        loading.className = 'loading-dots';
        loading.textContent = '...';
        chatHistory.appendChild(loading);
        scrollToBottom();
    }
    
    function hideLoading() {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.remove();
        }
    }

    // Handle form submission
    chatForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const question = questionInput.value.trim();
        if (!question) return;

        // Disable form
        submitButton.disabled = true;
        questionInput.disabled = true;

        // 1. Add user's question to the UI
        addMessageEntry('user', question);
        questionInput.value = '';
        scrollToBottom();

        // 2. Show a loading indicator
        showLoading();

        try {
            // 3. Send the question to the backend API
            // --- UPDATED FETCH URL ---
            const response = await fetch('/api/chat/' + conversationId, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ question: question }),
            });

            if (!response.ok) {
                // If auth fails, Spring Security redirects to login page
                if (response.status === 401 || response.status === 403) {
                     window.location.href = "/login";
                }
                throw new Error('Network response was not ok');
            }

            const data = await response.json(); // data is the ChatEntry object

            // 4. Hide loading and add the AI's answer
            hideLoading();
            addMessageEntry('ai', data.answer, data.timestamp);
            
        } catch (error) {
            console.error('Error:', error);
            hideLoading();
            addMessageEntry('ai', 'Sorry, something went wrong. Please try again.');
        } finally {
            // Re-enable form
            submitButton.disabled = false;
            questionInput.disabled = false;
            questionInput.focus();
            scrollToBottom();
        }
    });
});