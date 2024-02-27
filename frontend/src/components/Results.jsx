import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

const Results = () => {
    const location = useLocation();
    const { responseBodyJson } = location.state;
    const labels = responseBodyJson['labels'];
    const facesNumber = responseBodyJson['facesNumber'];
    const imageDetectedFaces = responseBodyJson['imageDetectedFaces'];
    const sensitive = responseBodyJson['sensitive'];
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

    const sumSensitive = (sensitive) => {
        const percentages = Object.values(sensitive).map(value => {
            switch (value) {
                case "VERY_UNLIKELY":
                    return 0;
                case "UNLIKELY":
                    return 20;
                case "POSSIBLE":
                    return 35;
                case "LIKELY":
                    return 60;
                case "VERY_LIKELY":
                    return 80;
                default:
                    return 0;
            }
        });
        return percentages.reduce((total, value) => total + value, 0);
    };

    const totalBlur = () => {
        const violence = sensitive['violence'];
        const racy = sensitive['racy'];
        const adult = sensitive['adult'];
        const total = sumSensitive({ violence, racy, adult });

        if (violence === 'LIKELY' || violence === 'VERY_LIKELY' || total > 45) {
            return "blur(5px)";
        } if (racy === 'LIKELY' || racy === 'VERY_LIKELY' || total > 45) {
            return "blur(5px)";
        } if (adult === 'POSIBLE' || adult === 'LIKELY' || adult === 'VERY_LIKELY' || total > 45) {
            return "blur(5px)";
        } else {
            return "blur(0px)";
        }
    };

    const getMessage = () => {
        const total = sumSensitive(sensitive);
        if (total > 45) {
            return <span style={{ color: 'red' }}>Imagen no apta para la institución</span>;
        } else {
            return <span style={{ color: 'green' }}>Imagen válida</span>;
        }
    };

    return (
        <div>
            <div style={{ paddingTop: "5%", paddingLeft: "40%", paddingRight: "20%", textAlign: "center", width: "100vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <button style={{ marginRight: "300px", width: "75px" }} type="button" className="btn btn-outline-warning" onClick={metodoSubmit}><FontAwesomeIcon icon={faArrowLeft} style={{ marginRight: "0.5rem" }} /></button>
                <h1 style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingTop: "5%" }}>Resultados</h1>
            </div>

            <div style={{ textAlign: "center", fontSize: "20px", marginTop: "5vh" }}>{getMessage()}</div>

            <div style={{ marginLeft: "300px", marginRight: "300px", textAlign: "center", maxWidth: "100vw", height: "70vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <div style={{ marginRight: "10%", textAlign: "center", maxWidth: "100%", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                    <img
                        src={`data:image/jpeg;base64,${imageDetectedFaces}`}
                        alt="Detected Faces"
                        style={{
                            width: "180vw",
                            height: "50vh",
                            maxWidth: "100%",
                            backgroundColor: "#d9e3f1",
                            borderRadius: "80px",
                            objectFit: "cover",
                            boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)",
                            filter: totalBlur()
                        }}
                    />
                </div>
                <div style={{ maxWidth: "100%", height: "50vh", width: "80vw" }}>
                    <p style={{
                        height: "100%",
                        backgroundColor: "#d9e3f1",
                        borderRadius: "80px",
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                        alignItems: "center",
                        fontSize: "20px",
                        color: "#485785",
                        font: "small-caption",
                        boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)"
                    }} className="lead">
                        <span>Cantidad de rostros detectados</span>
                        <span style={{ paddingTop: "5%", fontSize: "64px" }}>{facesNumber}</span>
                    </p>
                </div>
            </div>

            <div style={{ marginLeft: "300px", marginRight: "300px", textAlign: "center", maxWidth: "100vw", height: "5vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <p style={{ textAlign: "center", height: "1vh", display: "flex", justifyContent: "center", alignItems: "center" }} className="lead">Etiquetas detectadas</p>
                <p style={{ marginLeft: "40%", textAlign: "center", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }} className="lead">Contenido sensible</p>
            </div>

            <div style={{ marginLeft: "300px", marginRight: "300px", marginBottom: "10%", textAlign: "center", maxWidth: "100vw", height: "60vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <div style={{ marginRight: "10%", maxWidth: "100%", height: "50vh", width: "100vw", borderRadius: "50px", boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)" }}>
                    <table style={{ overflow: "hidden", textAlign: "center", borderRadius: "30px" }} className="table table-hover">
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
                                    <td>
                                        <div style={{ width: "100%", backgroundColor: "#ddd", borderRadius: "10px", textAlign: "center" }}>
                                            <div style={{ width: `${item.score}%`, backgroundColor: "#75f760", borderRadius: "10px", color: "white" }}>
                                                {item.score}
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div style={{ marginLeft: "5%", maxWidth: "100%", height: "50vh", width: "100vw", borderRadius: "50px", boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)" }}>
                    <table style={{ overflow: "hidden", textAlign: "center", borderRadius: "30px" }} className="table table-hover">
                        <thead>
                            <tr className="table-dark">
                                <th scope="col">Tipo</th>
                                <th scope="col">Coincidencia</th>
                                <th scope="col">Porcentaje (%)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {Object.keys(sensitive).map((type, index) => (
                                <tr className="table-type" key={index}>
                                    <td>{type}</td>
                                    <td style={{ height: "9.2vh", backgroundColor: getColor(sensitive[type]) }}>{sensitive[type]}</td>
                                    <td style={{ height: "9.2vh", backgroundColor: getColor(sensitive[type]) }}>{sumSensitive({type:sensitive[type]})}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Results;
