
//Sensitivity events
deltaTimeField.addEventListener("change", () => {
    console.log("deltaTimeField " + deltaTimeField.value)
    sessionStorage.setItem("sensitivity_time", deltaTimeField.value);
  });

deltaMovementField.addEventListener("change", () => {
    console.log("deltaMovementField " + deltaMovementField.value)
    sessionStorage.setItem("sensitivity_movement", deltaMovementField.value);
  });

fpsField.addEventListener("change", () => {
    console.log("fpsField " + fpsField.value)
    sessionStorage.setItem("frames_per_second", fpsField.value);
  });

