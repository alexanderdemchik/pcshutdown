const {exec} = require('child_process');

// Originally inspired by  David Walsh (https://davidwalsh.name/javascript-debounce-function)

// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// `wait` milliseconds.
function debounce(func, wait) {
  let timeout;

  // This is the function that is returned and will be executed many times
  // We spread (...args) to capture any number of parameters we want to pass
  return function executedFunction(...args) {

    // The callback function to be executed after 
    // the debounce time has elapsed
    const later = () => {
      // null timeout to indicate the debounce ended
      timeout = null;
      
      // Execute the callback
      func(...args);
    };
    // This will reset the waiting every function execution.
    // This is the step that prevents the function from
    // being executed because it will never reach the 
    // inside of the previous setTimeout  
    clearTimeout(timeout);
    
    // Restart the debounce waiting period.
    // setTimeout returns a truthy value (it differs in web vs Node)
    timeout = setTimeout(later, wait);
  };
};

function killAll(pid, signal = 'SIGTERM') {
  if(process.platform == "win32") {
      exec(`taskkill /PID ${pid} /T /F`, (error, stdout, stderr)=>{});
  } else{
      // see https://nodejs.org/api/child_process.html#child_process_options_detached
      // If pid is less than -1, then sig is sent to every process in the process group whose ID is -pid.
      process.kill(-pid, signal)
  }
}

module.exports = {debounce, killAll};
