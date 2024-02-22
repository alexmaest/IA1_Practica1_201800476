import React, { useState } from "react";

const API = 'http://localhost:8080';

const Screen = () => {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
  };

  const metodoSubmit = async () => {
     fetch(`${API}/data/`, {
       method: "POST",
       headers: {"Content-Type": "application/json"},
       body: JSON.stringify({data : "Hola mi pana"})
     });
   };

  return (
    <div>
      <h1 style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingTop: "5%" }}>Cloud Vision API</h1>
      <div style={{ textAlign: "center", height: "70vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
          <label htmlFor="fileInput">
            <div 
              style={{
                width: "40vw",
                height: "50vh",
                backgroundColor: "#d9e3f1",
                borderRadius: "80px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                cursor: "pointer",
                fontSize: "24px",
                color: "#485785",
                font: "small-caption",
                boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)" // Agregar sombra y brillo
              }}
            >
              {selectedFile ? (
                <img 
                  src={URL.createObjectURL(selectedFile)} 
                  alt="Preview" 
                  style={{ width: "100%", height: "100%", objectFit: "cover", borderRadius: "10px" }} 
                />
              ) : (
                "Cargar imagen"
              )}
            </div>
          </label>
          <input 
            id="fileInput"
            type="file" 
            style={{ display: "none" }} 
            onChange={handleFileChange} 
          />
        </div>
      <div style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingBottom: "10%"}}>
        <button style={{ width: "75vh"}} type="button" class="btn btn-outline-primary" onClick={metodoSubmit}>Procesar</button>
        </div>

    </div>
  );
};

export default Screen;
