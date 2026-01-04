# language: fr
Fonctionnalité: Gestion des utilisateurs
  En tant qu'administrateur
  Je veux gérer les utilisateurs
  Afin de maintenir la base de données

  Scénario: Créer un nouvel utilisateur
    Étant donné une base de données vide
    Quand je crée un utilisateur "Ahmed" avec le nom "Ben Ali" et l'email "ahmed@email.com"
    Alors l'utilisateur "Ahmed" existe dans la base

  Scénario: Lister tous les utilisateurs
    Étant donné les utilisateurs suivants existent:
      | firstName | lastName | email           | phoneNumber |
      | Ahmed     | Ben Ali  | ahmed@email.com | 12345678    |
      | Fatma     | Ben Ahmed| fatma@email.com | 87654321    |
    Quand je demande la liste des utilisateurs
    Alors je reçois 2 utilisateurs

  Scénario: Supprimer un utilisateur
    Étant donné un utilisateur "Ahmed" existe avec l'ID 1
    Quand je supprime l'utilisateur avec l'ID 1
    Alors l'utilisateur avec l'ID 1 n'existe plus