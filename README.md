# AWS 로컬 개발 환경 설정 가이드 (IAM Access Key 방식)

이 문서는 로컬 환경에서 **AWS SDK v2 (S3 등)** 를 사용하기 위해 필요한  
기본 자격증명 설정 방법을 설명합니다.

---

## 1. IAM에서 액세스 키 생성

1. AWS 콘솔 접속
2. **IAM → 사용자(User)** 선택
3. 사용할 사용자 클릭
4. **보안 자격 증명(Security credentials)** 탭 이동
5. **액세스 키 생성(Create access key)** 클릭
6. 사용 목적은 **로컬 코드(Local code)** 선택
7. 아래 두 값을 발급받음
   - Access Key ID
   - Secret Access Key

⚠️ **Secret Access Key는 다시 확인할 수 없으므로 반드시 안전하게 보관**

---

## 2. `~/.aws/credentials` 파일 생성
## 3. ~/.aws/config 파일 생성
### 파일 위치
- **Windows**
  C:\Users\사용자이름\.aws\credentials
  C:\Users\사용자이름\.aws\config
- **macOS / Linux**
  ~/.aws/credentials
  ~/.aws/config
  

### 파일 내용
```ini
[default]
aws_access_key_id=AKIAxxxxxxxxxxxx
aws_secret_access_key=xxxxxxxxxxxxxxxxxxxxxxxx

```ini
[default]
region=ap-northeast-2
