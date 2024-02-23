import React, { useState } from "react";
import { useHistory } from 'react-router-dom'; // Importa useHistory

const API = 'http://localhost:8080';

const Form = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const history = useHistory(); // Usa el hook useHistory en lugar de withRouter

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
  };

  const metodoSubmit = async () => {
    if (!selectedFile) {
      alert("Por favor, seleccione una imagen.");
      return;
    }

    const reader = new FileReader();
    reader.onload = async () => {
      const base64String = reader.result.split(",")[1];
      try {
        const response = await fetch(`${API}/data/`, {
          method: "POST",
          headers: {
            "Content-Type": "application/plain",
          },
          body: base64String,
        });

        if (response.ok) {
           console.log("Imagen enviada exitosamente.");
          const responseBodyJson = await response.json();
          console.log(responseBodyJson);
          history.push({
            pathname: '/results',
            state: { responseBodyJson } // Pasar responseBodyJson como parte del estado de la ubicación
          });
          window.location.reload();
        } else {
          console.error("Error al enviar la imagen:", response.statusText);
        }
      } catch (error) {
        console.error("Error de red:", error);
      }
    };
    reader.readAsDataURL(selectedFile);
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
              fontSize: "22px",
              color: "#485785",
              font: "small-caption",
              boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)"
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
      <div style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingBottom: "10%" }}>
        <button style={{ width: "75vh" }} type="button" className="btn btn-outline-dark" onClick={metodoSubmit}>Procesar</button>
      </div>
    </div>
  );
};

export default Form;
