import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

const Results = () => {
    const location = useLocation();
    const { responseBodyJson } = location.state;
    var labels = responseBodyJson['labels'];
    var facesNumber = responseBodyJson['facesNumber'];
    var imageDetectedFaces = responseBodyJson['imageDetectedFaces'];
    var sensitive = responseBodyJson['sensitive'];
    console.log(sensitive);
    const history = useHistory();

    const metodoSubmit = async () => {
        history.push({
            pathname: '/',
            state: { responseBodyJson }
        });
        window.location.reload();
    };

    const getColor = (value) => {
        switch (value) {
            case "VERY_UNLIKELY":
                return "#f76d6d";
            case "UNLIKELY":
                return "#f7b36f";
            case "POSSIBLE":
                return "#f0d560";
            case "LIKELY":
                return "#c9f56c";
            case "VERY_LIKELY":
                return "#75f760";
            default:
                return "#000000";
        }
    };

    return (
        <div>
            <div style={{ paddingTop: "5%", paddingLeft: "40%", paddingRight: "20%", textAlign: "center", width: "100vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <button style={{ marginRight: "300px", width: "75px" }} type="button" className="btn btn-outline-warning" onClick={metodoSubmit}><FontAwesomeIcon icon={faArrowLeft} style={{ marginRight: "0.5rem" }} /></button>
                <h1 style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingTop: "5%" }}>Resultados</h1>
            </div>

            <p style={{ textAlign: "center", height: "20vh", display: "flex", justifyContent: "center", alignItems: "center" }} className="lead">Cantidad de rostros detectados: {facesNumber}</p>

            <div style={{ textAlign: "center", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
    <img
        src={`data:image/jpeg;base64,${imageDetectedFaces}`}
        alt="Detected Faces"
        style={{
            width: "40vw",
            height: "50vh",
            maxWidth: "100%",
            backgroundColor: "#d9e3f1",
            borderRadius: "80px",
            objectFit: "cover",
            boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)"
        }}
    />
</div>


            <p style={{ textAlign: "center", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }} className="lead">Contenido sensible</p>

            <div style={{ paddingLeft: "20%", paddingRight: "20%", paddingTop: "1%", textAlign: "center", height: "5vh", width: "200vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <table style={{ borderRadius: "10px", overflow: "hidden", textAlign: "center" }} className="table table-hover">
                    <thead>
                        <tr className="table-dark">
                            <th scope="col">Tipo</th>
                            <th scope="col">Coincidencia</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Object.keys(sensitive).map((type, index) => (
                            <tr className="table-type" key={index}>
                                <td>{type}</td>
                                <td style={{ backgroundColor: getColor(sensitive[type]) }}>{sensitive[type]}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <p style={{ textAlign: "center", height: "50vh", display: "flex", justifyContent: "center", alignItems: "center" }} className="lead">Etiquetas detectadas</p>

            <div style={{ paddingLeft: "20%", paddingRight: "20%", paddingTop: "5%", paddingBottom: "20%", textAlign: "center", height: "40vh", width: "200vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <table style={{ borderRadius: "10px", overflow: "hidden", textAlign: "center" }} className="table table-hover">
                    <thead>
                        <tr className="table-dark">
                            <th scope="col">Etiqueta</th>
                            <th scope="col">Porcentaje (%)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {labels.map((item, index) => (
                            <tr className="table-type" key={index}>
                                <td>{item.description}</td>
                                <td>{item.score}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Results;
