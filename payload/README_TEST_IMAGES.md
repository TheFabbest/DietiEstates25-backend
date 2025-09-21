# Test Images for Property Creation

## File disponibili

Sono stati creati i seguenti file immagine WEBP per i test:

- `test_image1.webp` - Immagine di test 1
- `test_image2.webp` - Immagine di test 2  
- `test_image3.webp` - Immagine di test 3

## Come usare le immagini nei test

I file WEBP possono essere utilizzati come immagini nei test di creazione proprietà. Il sistema accetta solo file in formato WEBP.

### Esempio di comando curl per test con immagini

```bash
curl -X POST "http://localhost:8080/api/properties" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: multipart/form-data" \
  -F "property=@payload/residential.json;type=application/json" \
  -F "images=@payload/test_image1.webp;type=image/webp" \
  -F "images=@payload/test_image2.webp;type=image/webp"
```

### Note importanti

1. **Formato obbligatorio**: Il sistema accetta solo file WEBP
2. **Validazione**: Le immagini vengono validate dal servizio `ImageValidationService`
3. **Dimensione**: I file di test sono vuoti, per test reali usare immagini WEBP valide
4. **Directory immagini**: Le immagini vengono caricate in directory con ULID su Azure Blob Storage

## Sostituzione con immagini reali

Per test più realistici, sostituire i file WEBP vuoti con immagini WEBP reali:

1. Convertire immagini JPEG/PNG in WEBP
2. Mantenere dimensioni ragionevoli (max 5MB per immagine)
3. Verificare che siano immagini valide

## Validazione immagini

Il sistema valida:
- Formato (solo WEBP)
- Dimensione (configurata in application.properties)
- Tipo MIME (image/webp)