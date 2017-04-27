# -*- coding: utf-8 -*-
"""
Created on Mon Mar 27 00:35:26 2017

@author: LUXIAOTIAN
"""

import smtplib
from email.mime.text import MIMEText
SMTP_SERVER = "smtp.163.com"
SMTP_PORT = 25
SMTP_USERNAME = "18937109768"
SMTP_PASSWORD = "lxt19960201"
EMAIL_FROM = "18937109768@163.com"
EMAIL_TO = "lxtian@umich.edu"
EMAIL_SUBJECT = "Secure Cloud Manufacturing notification"
co_msg = """
499 points are clustered as frequency 40
300 points are clustered as frequency 30
anomaly detected
"""
def send_email():
    msg = MIMEText(co_msg)
    msg['Subject'] = EMAIL_SUBJECT
    msg['From'] = EMAIL_FROM 
    msg['To'] = EMAIL_TO
    debuglevel = True
    try:
        mail = smtplib.SMTP(SMTP_SERVER, SMTP_PORT)
    except:
        print('fail to ')
    mail.set_debuglevel(debuglevel)
    mail.starttls()
    try:
        mail.login(SMTP_USERNAME, SMTP_PASSWORD)
    except:
        print('nonono')    
    mail.sendmail(EMAIL_FROM, EMAIL_TO, msg.as_string())
    mail.quit()

if __name__=='__main__':
    send_email()