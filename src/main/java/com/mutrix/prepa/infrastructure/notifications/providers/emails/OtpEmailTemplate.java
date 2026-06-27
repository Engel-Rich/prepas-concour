package com.mutrix.prepa.infrastructure.notifications.providers.emails;

/**
 * Générateur de template HTML pour les e-mails OTP de Prepa Concours.
 */
public final class OtpEmailTemplate {

    private OtpEmailTemplate() {}

    /**
     * Construit un e-mail HTML complet avec le code OTP mis en évidence.
     *
     * @param otp      le code à 6 chiffres
     * @param fullName prénom / nom du destinataire (optionnel – peut être null)
     * @return chaîne HTML prête à envoyer via Resend
     */
    public static String build(String otp, String fullName) {
        String greeting = (fullName != null && !fullName.isBlank())
                ? "Bonjour <strong>" + escapeHtml(fullName) + "</strong>,"
                : "Bonjour,";

        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width,initial-scale=1.0" />
                  <title>Code de vérification – Prepa Concours</title>
                </head>
                <body style="margin:0;padding:0;background:#f0f4f8;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f4f8;padding:48px 16px;">
                    <tr>
                      <td align="center">
                        <table width="560" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:16px;overflow:hidden;
                                      box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:560px;width:100%%;">

                          <!-- ── HEADER ─────────────────────────────────── -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#1d4ed8 0%%,#1e3a8a 100%%);
                                        padding:40px 48px;text-align:center;">
                              <p style="margin:0 0 4px;font-size:11px;font-weight:700;
                                         color:rgba(255,255,255,0.55);letter-spacing:3px;text-transform:uppercase;">
                                Plateforme de préparation
                              </p>
                              <h1 style="margin:0;font-size:28px;font-weight:800;color:#ffffff;letter-spacing:-0.5px;">
                                Prepa <span style="color:#93c5fd;">Concours</span>
                              </h1>
                            </td>
                          </tr>

                          <!-- ── BODY ──────────────────────────────────── -->
                          <tr>
                            <td style="padding:40px 48px 32px;">
                              <p style="margin:0 0 20px;font-size:15px;color:#374151;line-height:1.6;">
                                %s
                              </p>
                              <p style="margin:0 0 28px;font-size:15px;color:#6b7280;line-height:1.6;">
                                Voici votre code de vérification pour finaliser votre inscription.
                                Ce code est valable <strong style="color:#111827;">10 minutes</strong>.
                              </p>

                              <!-- OTP BOX -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:28px;">
                                <tr>
                                  <td style="background:#eff6ff;border:2px dashed #3b82f6;border-radius:12px;
                                              padding:28px;text-align:center;">
                                    <p style="margin:0 0 6px;font-size:11px;font-weight:700;color:#3b82f6;
                                               text-transform:uppercase;letter-spacing:2px;">
                                      Votre code de vérification
                                    </p>
                                    <p style="margin:0;font-size:44px;font-weight:800;letter-spacing:14px;
                                               color:#1e3a8a;font-family:'Courier New',monospace;">
                                      %s
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <!-- WARNING BOX -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                                <tr>
                                  <td style="background:#fff7ed;border-left:4px solid #f97316;
                                              border-radius:0 8px 8px 0;padding:14px 16px;">
                                    <p style="margin:0;font-size:13px;color:#92400e;line-height:1.5;">
                                      ⚠️ <strong>Ne partagez jamais ce code</strong>, même avec un agent Prepa Concours.
                                      Nous ne vous demanderons jamais votre code par téléphone ou e-mail.
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <p style="margin:0;font-size:13px;color:#9ca3af;line-height:1.6;">
                                Si vous n'avez pas créé de compte sur Prepa Concours, ignorez simplement cet e-mail.
                                Aucune action n'est requise.
                              </p>
                            </td>
                          </tr>

                          <!-- ── DIVIDER ────────────────────────────────── -->
                          <tr>
                            <td style="padding:0 48px;">
                              <hr style="border:none;border-top:1px solid #e5e7eb;margin:0;" />
                            </td>
                          </tr>

                          <!-- ── FOOTER ─────────────────────────────────── -->
                          <tr>
                            <td style="padding:24px 48px;text-align:center;">
                              <p style="margin:0 0 4px;font-size:12px;color:#9ca3af;">
                                © %d Prepa Concours · Tous droits réservés
                              </p>
                              <p style="margin:0;font-size:12px;color:#d1d5db;">
                                Cet e-mail est généré automatiquement – merci de ne pas y répondre.
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                greeting,
                escapeHtml(otp),
                java.time.Year.now().getValue()
        );
    }

    /** Overload sans nom (utilise un salut générique). */
    public static String build(String otp) {
        return build(otp, null);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
